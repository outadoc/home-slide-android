package fr.outadoc.homeslide.app.feature.grid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.faltenreich.skeletonlayout.applySkeleton
import com.github.ajalt.timberkt.Timber
import com.google.android.material.snackbar.Snackbar
import fr.outadoc.homeslide.app.BuildConfig
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.app.feature.details.ui.EntityDetailFragment
import fr.outadoc.homeslide.app.feature.slideover.ui.EntityTileAdapter
import fr.outadoc.homeslide.app.feature.slideover.ui.SlideOverNavigator
import fr.outadoc.homeslide.app.onboarding.OnboardingActivity
import fr.outadoc.homeslide.app.preferences.AppPreferencesFragment
import fr.outadoc.homeslide.common.extensions.setupToolbar
import fr.outadoc.homeslide.common.feature.grid.vm.EntityGridViewModel
import fr.outadoc.homeslide.hassapi.model.entity.Entity
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class EntityGridFragment : Fragment() {

    private var viewHolder: ViewHolder? = null

    private val vm: EntityGridViewModel by viewModel()

    private val handler: Handler = Handler()
    private var menu: Menu? = null

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private val navigator: SlideOverNavigator?
        get() = parentFragment as? SlideOverNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedCallback = requireActivity()
            .onBackPressedDispatcher
            .addCallback(this) {
                vm.onBackPressed()
            }
    }

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_entity_grid, container, false)
        setupToolbar(R.string.title_quick_access, false)

        viewHolder = ViewHolder(
            root,
            EntityTileAdapter(
                onItemClickListener = {
                    vm.onEntityClick(it)
                },
                onReorderedListener = vm::onReorderedEntities,
                onItemLongPressListener = ::onItemLongPress,
                onItemVisibilityChangeListener = vm::onItemVisibilityChange
            ),
            EditingModeCallback(
                isEnabled = {
                    vm.editionState.value == EntityGridViewModel.EditionState.Editing
                }
            )
        )

        viewHolder?.let { setWindowInsets(it) }

        vm.gridState.observe(viewLifecycleOwner) { state ->
            viewHolder?.apply {
                when (state) {
                    EntityGridViewModel.GridState.Content -> {
                        noContentView.isGone = true
                        if (skeleton.isSkeleton()) {
                            skeleton.showOriginal()
                        }
                    }

                    EntityGridViewModel.GridState.Skeleton -> {
                        noContentView.isGone = true
                        skeleton.showSkeleton()
                    }

                    EntityGridViewModel.GridState.NoContent -> {
                        if (skeleton.isSkeleton()) {
                            skeleton.showOriginal()
                        }

                        noContentView.isVisible = true
                    }
                }
            }
        }

        vm.result.observe(viewLifecycleOwner) { shortcuts ->
            shortcuts
                .onFailure { e ->
                    val message = e.localizedMessage
                        ?.let { getString(R.string.grid_snackbar_loading_error_title, it) }
                        ?: getString(R.string.grid_snackbar_generic_error_title)

                    viewHolder?.recyclerView?.let {
                        Snackbar.make(it, message, Snackbar.LENGTH_LONG)
                            .setAction(R.string.grid_snackbar_error_action_retry) {
                                cancelRefresh()
                                vm.loadShortcuts()
                            }
                            .show()
                    }
                }

            scheduleRefresh()
        }

        vm.tiles.observe(viewLifecycleOwner) { shortcuts ->
            viewHolder?.itemAdapter?.apply {
                submitList(shortcuts)
            }
        }

        vm.editionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                EntityGridViewModel.EditionState.Editing -> {
                    onBackPressedCallback.isEnabled = true
                    cancelRefresh()
                }
                else -> {
                    onBackPressedCallback.isEnabled = false
                    scheduleRefresh()
                }
            }

            menu?.forEach { item ->
                when (item.itemId) {
                    R.id.menuItem_done -> item.isVisible =
                        state == EntityGridViewModel.EditionState.Editing
                    R.id.menuItem_edit -> item.isVisible =
                        state == EntityGridViewModel.EditionState.Normal
                }
            }

            viewHolder?.itemAdapter?.apply {
                this.isEditingMode = state == EntityGridViewModel.EditionState.Editing
                notifyDataSetChanged()
            }
        }

        vm.shouldAskForInitialValues.observe(viewLifecycleOwner) { shouldAskForInitialValues ->
            if (shouldAskForInitialValues) {
                startOnboarding()
            }
        }

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_grid_main, menu)
        this.menu = menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuItem_edit -> {
                vm.onEditClick()
                true
            }

            R.id.menuItem_settings -> {
                openSettings()
                true
            }

            R.id.menuItem_done -> {
                vm.onEditClick()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onItemLongPress(entity: Entity): Boolean {
        if (!BuildConfig.ENABLE_DETAILS) {
            return false
        }

        EntityDetailFragment.newInstance(entity)?.let {
            navigator?.apply {
                navigateTo(it)
                collapseSheet()
            }

            return true
        }

        return false
    }

    private fun scheduleRefresh() {
        // Only one refresh scheduled at once
        cancelRefresh()

        Timber.d { "scheduling refresh" }
        handler.postDelayed(TimeUnit.SECONDS.toMillis(vm.refreshIntervalSeconds)) {
            vm.loadShortcuts()
        }
    }

    private fun cancelRefresh() {
        Timber.d { "canceling refresh" }
        handler.removeCallbacksAndMessages(null)
    }

    private fun startOnboarding() {
        Intent(activity, OnboardingActivity::class.java).let { i ->
            startActivity(i)
            activity?.finish()
        }
    }

    private fun openSettings() {
        navigator?.navigateTo(AppPreferencesFragment.newInstance())
    }

    private fun setWindowInsets(viewHolder: ViewHolder) {
        with(viewHolder) {
            ViewCompat.setOnApplyWindowInsetsListener(recyclerView) { v, insets ->
                v.setPadding(
                    v.paddingLeft,
                    v.paddingTop,
                    v.paddingRight,
                    v.paddingBottom + insets.systemWindowInsetBottom
                )

                WindowInsetsCompat.Builder()
                    .setSystemWindowInsets(
                        Insets.of(
                            insets.systemWindowInsetLeft,
                            insets.systemWindowInsetTop,
                            insets.systemWindowInsetRight,
                            0
                        )
                    )
                    .build()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        activity?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onPause() {
        super.onPause()
        cancelRefresh()
    }

    override fun onResume() {
        super.onResume()
        vm.loadShortcuts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewHolder = null
    }

    private class ViewHolder(
        root: View,
        val itemAdapter: EntityTileAdapter,
        callback: EditingModeCallback
    ) {
        val itemTouchHelper = ItemTouchHelper(callback)

        val noContentView: View = root.findViewById(R.id.layout_noContent)
        val recyclerView: RecyclerView =
            root.findViewById<RecyclerView>(R.id.recyclerView_shortcuts).apply {
                val gridLayout = GridAutoSpanLayoutManager(
                    context,
                    resources.getDimension(R.dimen.item_width).toInt()
                )

                adapter = itemAdapter
                layoutManager = gridLayout

                addItemDecoration(
                    GridSpacingItemDecoration(
                        gridLayout,
                        resources.getDimensionPixelSize(R.dimen.grid_spacing)
                    )
                )
            }.also {
                itemTouchHelper.attachToRecyclerView(it)
            }

        val skeleton =
            recyclerView.applySkeleton(R.layout.item_shortcut, SKELETON_ITEM_COUNT).apply {
                maskColor = ContextCompat.getColor(recyclerView.context, R.color.skeleton_maskColor)
                shimmerColor =
                    ContextCompat.getColor(recyclerView.context, R.color.skeleton_shimmerColor)
            }
    }

    companion object {
        fun newInstance() = EntityGridFragment()

        private const val SKELETON_ITEM_COUNT = 30
    }
}