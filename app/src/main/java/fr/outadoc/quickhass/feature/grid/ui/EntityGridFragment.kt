package fr.outadoc.quickhass.feature.grid.ui

import android.content.Context
import android.content.Intent
import android.os.*
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.snackbar.Snackbar
import fr.outadoc.quickhass.BuildConfig
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.extensions.setupToolbar
import fr.outadoc.quickhass.feature.details.ui.EntityDetailFragment
import fr.outadoc.quickhass.feature.grid.vm.EntityGridViewModel
import fr.outadoc.quickhass.feature.onboarding.OnboardingActivity
import fr.outadoc.quickhass.feature.slideover.ui.EntityTileAdapter
import fr.outadoc.quickhass.feature.slideover.ui.SlideOverNavigator
import fr.outadoc.quickhass.model.entity.Entity
import fr.outadoc.quickhass.preferences.AppPreferencesFragment
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class EntityGridFragment : Fragment() {

    private var viewHolder: ViewHolder? = null

    private val vm: EntityGridViewModel by viewModel()
    private val vibrator: Vibrator by inject()

    private val handler: Handler = Handler()
    private var menu: Menu? = null

    private val navigator: SlideOverNavigator?
        get() = parentFragment as? SlideOverNavigator

    private val itemTouchHelper by lazy {
        ItemTouchHelper(EditingModeCallback(vm))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_entity_grid, container, false)
        setupToolbar(R.string.title_quick_access, false)

        viewHolder = ViewHolder(
            root,
            EntityTileAdapter(
                onItemClickListener = {
                    vibrate()
                    vm.onEntityClick(it)
                },
                onReorderedListener = vm::onReorderedEntities,
                onItemLongPress = ::onItemLongPress
            )
        )

        viewHolder?.recyclerView?.let {
            itemTouchHelper.attachToRecyclerView(it)
        }

        viewHolder?.let { setWindowInsets(it) }

        vm.result.observe(viewLifecycleOwner) { shortcuts ->
            shortcuts
                .onFailure { e ->
                    showRecyclerViewIfContent()

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

                    scheduleRefresh()
                }
        }

        vm.tiles.observe(viewLifecycleOwner) { shortcuts ->
            viewHolder?.itemAdapter?.apply {
                submitList(shortcuts)
            }

            showRecyclerViewIfContent()
            scheduleRefresh()
        }

        vm.isLoading.observe(viewLifecycleOwner) { isLoading ->
            viewHolder?.apply {
                if (!isLoading && skeleton.isSkeleton()) {
                    skeleton.showOriginal()
                }
            }
        }

        vm.editionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                EntityGridViewModel.State.Editing -> cancelRefresh()
                else -> scheduleRefresh()
            }

            menu?.forEach { item ->
                when (item.itemId) {
                    R.id.menuItem_done -> item.isVisible = state == EntityGridViewModel.State.Editing
                    R.id.menuItem_edit -> item.isVisible = state == EntityGridViewModel.State.Normal
                }
            }

            viewHolder?.itemAdapter?.apply {
                this.isEditingMode = state == EntityGridViewModel.State.Editing
                notifyDataSetChanged()
            }
        }

        vm.shouldAskForInitialValues.observe(viewLifecycleOwner) { shouldAskForInitialValues ->
            if (shouldAskForInitialValues) {
                startOnboarding()
            }
        }

        viewHolder?.skeleton?.showSkeleton()

        return root
    }

    private fun showRecyclerViewIfContent() {
        viewHolder?.apply {
            val hasContent = itemAdapter.itemCount > 0
            viewHolder?.noContent?.isVisible = !hasContent
        }
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

        handler.postDelayed(TimeUnit.SECONDS.toMillis(vm.refreshIntervalSeconds)) {
            vm.loadShortcuts()
        }
    }

    private fun cancelRefresh() {
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

                insets.replaceSystemWindowInsets(
                    insets.systemWindowInsetLeft,
                    insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight,
                    0
                )
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

    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(CLICK_VIBRATION_LENGTH_MS, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(CLICK_VIBRATION_LENGTH_MS)
        }
    }

    private class ViewHolder(root: View, val itemAdapter: EntityTileAdapter) {
        val noContent: View = root.findViewById(R.id.layout_noContent)
        val recyclerView: RecyclerView = root.findViewById<RecyclerView>(R.id.recyclerView_shortcuts).apply {
            val gridLayout = GridAutoSpanLayoutManager(context, resources.getDimension(R.dimen.item_height).toInt())

            adapter = itemAdapter
            layoutManager = gridLayout

            addItemDecoration(
                GridSpacingItemDecoration(gridLayout, resources.getDimensionPixelSize(R.dimen.grid_spacing))
            )
        }

        val skeleton = recyclerView.applySkeleton(R.layout.item_shortcut, SKELETON_ITEM_COUNT).apply {
            maskColor = ContextCompat.getColor(recyclerView.context, R.color.skeleton_maskColor)
            shimmerColor = ContextCompat.getColor(recyclerView.context, R.color.skeleton_shimmerColor)
        }
    }

    companion object {
        fun newInstance() = EntityGridFragment()

        private const val SKELETON_ITEM_COUNT = 30
        private const val CLICK_VIBRATION_LENGTH_MS = 50L
    }

}