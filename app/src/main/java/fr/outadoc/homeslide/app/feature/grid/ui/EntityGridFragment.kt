package fr.outadoc.homeslide.app.feature.grid.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkBuilder
import androidx.recyclerview.widget.ItemTouchHelper
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.github.ajalt.timberkt.Timber
import com.google.android.material.snackbar.Snackbar
import fr.outadoc.homeslide.app.BuildConfig
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.app.databinding.FragmentEntityGridBinding
import fr.outadoc.homeslide.app.feature.details.ui.EntityDetailFragment
import fr.outadoc.homeslide.app.feature.slideover.ui.EntityTileAdapter
import fr.outadoc.homeslide.app.feature.slideover.ui.SlideOverNavigator
import fr.outadoc.homeslide.app.onboarding.OnboardingActivity
import fr.outadoc.homeslide.app.preferences.AppPreferencesFragment
import fr.outadoc.homeslide.common.extensions.setupToolbar
import fr.outadoc.homeslide.common.feature.grid.vm.EntityListViewModel
import fr.outadoc.homeslide.common.feature.grid.vm.EntityListViewModel.Event
import fr.outadoc.homeslide.common.feature.grid.vm.EntityListViewModel.State
import fr.outadoc.homeslide.hassapi.model.entity.Entity
import io.uniflow.androidx.flow.onEvents
import io.uniflow.androidx.flow.onStates
import io.uniflow.core.flow.getCurrentStateOrNull
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class EntityGridFragment : Fragment() {

    private val vm: EntityListViewModel by viewModel()

    private var binding: FragmentEntityGridBinding? = null
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var skeleton: Skeleton? = null
    private var menu: Menu? = null

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private val navigator: SlideOverNavigator?
        get() = parentFragment as? SlideOverNavigator

    private val itemAdapter = EntityTileAdapter(
        onItemClickListener = {
            vm.onEntityClick(it)
        },
        onReorderedListener = { vm.onReorderedEntities(it) },
        onItemLongPressListener = ::onItemLongPress,
        onItemVisibilityChangeListener = { entity, isVisible ->
            vm.onItemVisibilityChange(entity, isVisible)
        },
        onItemHeightChangeListener = { itemHeight ->
            val chromeHeight = resources.getDimension(R.dimen.slideover_contentPeekHeight).toInt()
            navigator?.setPeekHeight((itemHeight * VISIBLE_ITEM_COUNT_VERTICAL) + chromeHeight)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedCallback = requireActivity()
            .onBackPressedDispatcher
            .addCallback(this) {
                vm.exitEditMode()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEntityGridBinding.inflate(inflater, container, false)

        setupToolbar(R.string.title_quick_access, false)

        val itemTouchHelper = ItemTouchHelper(
            EditingModeCallback(
                isEnabled = {
                    vm.getCurrentStateOrNull<State>() is State.Editing
                }
            )
        )

        binding?.apply {
            recyclerViewShortcuts.apply {
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

            skeleton = recyclerViewShortcuts.applySkeleton(
                listItemLayoutResId = R.layout.item_shortcut_shimmer,
                itemCount = SKELETON_ITEM_COUNT
            ).apply {
                maskColor = ContextCompat.getColor(
                    recyclerViewShortcuts.context,
                    R.color.skeleton_maskColor
                )
                shimmerColor =
                    ContextCompat.getColor(
                        recyclerViewShortcuts.context,
                        R.color.skeleton_shimmerColor
                    )
            }
        }

        binding?.setWindowInsets()

        onStates(vm) { state ->
            if (state is State) {
                updateContent(state)
                updateViewFlipper(state)
                updateEditingMode(state)
            }
        }

        onEvents(vm) { event ->
            when (val data = event.take()) {
                is Event.StartOnboarding -> startOnboarding(R.id.welcomeFragment)
                is Event.LoggedOut -> startOnboarding(R.id.setupHostFragment)
                is Event.Error -> displayError(data.e)
            }
        }

        return binding!!.root
    }

    private fun displayError(e: Throwable) {
        val message = e.localizedMessage
            ?.let { getString(R.string.grid_snackbar_loading_error_title, it) }
            ?: getString(R.string.grid_snackbar_generic_error_title)

        binding?.recyclerViewShortcuts?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.grid_snackbar_error_action_retry) {
                    cancelRefresh()
                    vm.loadEntities()
                }
                .show()
        }

        scheduleRefresh()
    }

    private fun startOnboarding(@IdRes destination: Int) {
        NavDeepLinkBuilder(requireActivity())
            .setComponentName(OnboardingActivity::class.java)
            .setGraph(R.navigation.nav_graph_onboarding)
            .setDestination(destination)
            .createPendingIntent()
            .send()

        activity?.finish()
    }

    private fun updateContent(state: State) {
        itemAdapter.apply {
            when (state) {
                is State.Content -> submitList(state.displayTiles)
                is State.Editing -> submitList(state.tiles)
                State.Empty -> submitList(emptyList())
                else -> Unit
            }
        }
    }

    private fun updateViewFlipper(state: State) {
        val childToDisplay = when (state) {
            is State.Editing,
            is State.Content -> {
                skeleton?.let { if (it.isSkeleton()) it.showOriginal() }
                CHILD_CONTENT
            }

            is State.Loading -> {
                skeleton?.let { if (!it.isSkeleton()) it.showSkeleton() }
                CHILD_CONTENT
            }

            is State.Empty -> {
                skeleton?.let { if (it.isSkeleton()) it.showOriginal() }
                CHILD_NO_CONTENT
            }
        }

        binding?.apply {
            if (viewFlipperEntityGrid.displayedChild != childToDisplay) {
                viewFlipperEntityGrid.displayedChild = childToDisplay
            }
        }
    }

    private fun updateEditingMode(state: State) {
        val isEditing = when (state) {
            is State.Editing -> {
                onBackPressedCallback.isEnabled = true
                cancelRefresh()
                true
            }
            else -> {
                onBackPressedCallback.isEnabled = false
                scheduleRefresh()
                false
            }
        }

        menu?.forEach { item ->
            when (item.itemId) {
                R.id.menuItem_done -> item.isVisible = isEditing
                R.id.menuItem_edit -> item.isVisible = !isEditing
            }
        }

        itemAdapter.isEditingMode = isEditing
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_grid_main, menu)
        this.menu = menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuItem_edit -> {
                vm.enterEditMode()
                true
            }

            R.id.menuItem_settings -> {
                openSettings()
                true
            }

            R.id.menuItem_done -> {
                vm.exitEditMode()
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
            vm.loadEntities()
        }
    }

    private fun cancelRefresh() {
        Timber.d { "canceling refresh" }
        handler.removeCallbacksAndMessages(null)
    }

    private fun openSettings() {
        navigator?.navigateTo(AppPreferencesFragment.newInstance())
    }

    private fun FragmentEntityGridBinding.setWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(recyclerViewShortcuts) { v, insets ->
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
        vm.loadEntities()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        skeleton = null
    }

    companion object {
        fun newInstance() = EntityGridFragment()

        private const val SKELETON_ITEM_COUNT = 30
        private const val VISIBLE_ITEM_COUNT_VERTICAL = 3

        private const val CHILD_CONTENT = 0
        private const val CHILD_NO_CONTENT = 1
    }
}