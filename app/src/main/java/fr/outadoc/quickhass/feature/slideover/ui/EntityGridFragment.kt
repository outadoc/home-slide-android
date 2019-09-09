package fr.outadoc.quickhass.feature.slideover.ui

import android.content.Context
import android.content.Intent
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.faltenreich.skeletonlayout.applySkeleton
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.onboarding.OnboardingActivity
import fr.outadoc.quickhass.feature.slideover.vm.EntityGridViewModel
import fr.outadoc.quickhass.preferences.SettingsActivity
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


class EntityGridFragment : Fragment() {

    private var viewHolder: ViewHolder? = null
    private val vm: EntityGridViewModel by viewModel()

    private val vibrator: Vibrator by inject()

    private val handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val doneToEditAvd = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.avd_done_to_edit)
        val editToDoneAvd = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.avd_edit_to_done)

        with(vm) {
            result.observe(this@EntityGridFragment, Observer { shortcuts ->
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
            })

            isLoading.observe(this@EntityGridFragment, Observer { isLoading ->
                viewHolder?.apply {
                    progress.isVisible = isLoading

                    if (!isLoading && skeleton.isSkeleton()) {
                        skeleton.showOriginal()
                    }
                }
            })

            isEditingMode.observe(this@EntityGridFragment, Observer { isEditingMode ->
                vibrate()

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
            })

            shouldAskForInitialValues.observe(
                this@EntityGridFragment,
                Observer { shouldAskForInitialValues ->
                    if (shouldAskForInitialValues) {
                        startOnboarding()
                    }
                })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_entity_grid, container, false)

        viewHolder = ViewHolder(
            root,
            EntityAdapter({
                vibrate()
                vm.onEntityClick(it)
            }, vm::onReorderedEntities)
        ).apply {
            settingsButton.setOnClickListener { openSettings() }
            editButton.setOnClickListener { vm.onEditClick() }

            itemTouchHelper.attachToRecyclerView(recyclerView)
            setWindowInsets(this)

            skeleton.showSkeleton()
        }

        return root
    }

    private fun scheduleRefresh() {
        // Only one refresh scheduled at once
        cancelRefresh()

        handler.postDelayed(REFRESH_INTERVAL_MS) {
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
        val progress: ProgressBar = root.findViewById(R.id.progress_main)

        val recyclerView: RecyclerView = root.findViewById<RecyclerView>(R.id.recyclerView_shortcuts).apply {
            adapter = itemAdapter
            layoutManager = GridLayoutManager(context, GRID_SPAN_COUNT)

            addItemDecoration(
                GridSpacingItemDecoration(
                    GRID_SPAN_COUNT,
                    resources.getDimensionPixelSize(R.dimen.grid_spacing)
                )
            )
        }

        val skeleton = recyclerView.applySkeleton(R.layout.item_shortcut, GRID_SPAN_COUNT * 15).apply {
            maskColor = ContextCompat.getColor(recyclerView.context, R.color.skeleton_maskColor)
            shimmerColor = ContextCompat.getColor(recyclerView.context, R.color.skeleton_shimmerColor)
        }
    }

    companion object {
        fun newInstance() = EntityGridFragment()

        const val GRID_SPAN_COUNT = 3
        const val REFRESH_INTERVAL_MS = 10000L
        const val CLICK_VIBRATION_LENGTH_MS = 50L
    }

}

