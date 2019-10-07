package fr.outadoc.quickhass.feature.grid.ui

import android.content.Context
import android.content.Intent
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.faltenreich.skeletonlayout.applySkeleton
import fr.outadoc.quickhass.BuildConfig
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.details.ui.EntityDetailFragment
import fr.outadoc.quickhass.feature.grid.vm.EntityGridViewModel
import fr.outadoc.quickhass.feature.onboarding.OnboardingActivity
import fr.outadoc.quickhass.feature.slideover.model.entity.Entity
import fr.outadoc.quickhass.feature.slideover.ui.EntityAdapter
import fr.outadoc.quickhass.feature.slideover.ui.SlideOverNavigator
import fr.outadoc.quickhass.preferences.SettingsActivity
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


class EntityGridFragment : Fragment() {

    private var viewHolder: ViewHolder? = null
    private val vm: EntityGridViewModel by viewModel()

    private val vibrator: Vibrator by inject()

    private val handler: Handler = Handler()

    private var isInitialEditing = true

    private val navigator: SlideOverNavigator?
        get() = parentFragment as? SlideOverNavigator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_entity_grid, container, false)

        val doneToEditAvd = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.avd_done_to_edit)
        val editToDoneAvd = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.avd_edit_to_done)

        viewHolder = ViewHolder(
            root,
            EntityAdapter({
                vibrate()
                vm.onEntityClick(it)
            }, vm::onReorderedEntities, ::onItemLongPress)
        ).apply {
            settingsButton.setOnClickListener { openSettings() }
            editButton.setOnClickListener { vm.onEditClick() }

            itemTouchHelper.attachToRecyclerView(recyclerView)
            setWindowInsets(this)

            skeleton.showSkeleton()
        }

        vm.result.observe(viewLifecycleOwner) { shortcuts ->
            shortcuts
                .onFailure { e ->
                    Toast.makeText(context, e.message ?: getString(R.string.toast_generic_error_title), Toast.LENGTH_SHORT).show()
                    scheduleRefresh()
                }
                .onSuccess {
                    viewHolder?.itemAdapter?.apply {
                        items.clear()
                        items.addAll(shortcuts.getOrDefault(emptyList()))
                        notifyDataSetChanged()
                    }

                    scheduleRefresh()
                }
        }

        vm.isLoading.observe(viewLifecycleOwner) { isLoading ->
            viewHolder?.apply {
                if (!isLoading && skeleton.isSkeleton()) {
                    skeleton.showOriginal()
                }
            }
        }

        vm.loadingEntityIds.observe(viewLifecycleOwner) { entityIds ->
            viewHolder?.itemAdapter?.loadingEntityIds = entityIds
        }

        vm.isEditingMode.observe(viewLifecycleOwner) { isEditingMode ->
            if (isInitialEditing) {
                isInitialEditing = false
            } else {
                vibrate()
            }

            if (isEditingMode) {
                cancelRefresh()
            } else {
                scheduleRefresh()
            }

            viewHolder?.apply {
                val drawable = when (isEditingMode) {
                    true -> editToDoneAvd
                    false -> doneToEditAvd
                }

                editButton.setImageDrawable(drawable)
                drawable?.start()
            }

            viewHolder?.itemAdapter?.apply {
                this.isEditingMode = isEditingMode
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

        handler.postDelayed(vm.refreshIntervalSeconds * 1000L) {
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
        startActivity(Intent(activity, SettingsActivity::class.java))
    }

    private fun setWindowInsets(viewHolder: ViewHolder) {
        with(viewHolder) {
            ViewCompat.setOnApplyWindowInsetsListener(recyclerView) { v, insets ->
                val newPaddingBottom = insets.systemWindowInsetBottom + root.paddingTop
                v.setPadding(0, 0, 0, newPaddingBottom)

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

    private val itemTouchHelper by lazy {
        ItemTouchHelper(EditingModeCallback(vm))
    }

    private class ViewHolder(val root: View, val itemAdapter: EntityAdapter) {
        val settingsButton: ImageButton = root.findViewById(R.id.imageButton_settings)
        val editButton: ImageButton = root.findViewById(R.id.imageButton_edit)

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

