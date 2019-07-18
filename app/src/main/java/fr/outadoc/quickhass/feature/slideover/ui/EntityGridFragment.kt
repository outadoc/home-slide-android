package fr.outadoc.quickhass.feature.slideover.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.onboarding.OnboardingActivity
import fr.outadoc.quickhass.feature.slideover.vm.EntityGridViewModel


class EntityGridFragment : Fragment() {

    companion object {
        fun newInstance() = EntityGridFragment()

        const val GRID_SPAN_COUNT = 3
        const val REFRESH_INTERVAL_MS = 10000L
    }

    private lateinit var viewModel: EntityGridViewModel

    private var viewHolder: ViewHolder? = null

    private val handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(EntityGridViewModel::class.java)

        with(viewModel) {
            result.observe(this@EntityGridFragment, Observer { shortcuts ->
                shortcuts
                    .onFailure { e ->
                        Toast.makeText(
                            context,
                            e.message ?: getString(R.string.toast_generic_error_title),
                            Toast.LENGTH_SHORT
                        ).show()

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
                }
            })

            isEditingMode.observe(this@EntityGridFragment, Observer { isEditingMode ->
                if (isEditingMode) {
                    cancelRefresh()
                } else {
                    scheduleRefresh()
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
                        Toast.makeText(
                            context,
                            R.string.toast_config_needed_title,
                            Toast.LENGTH_LONG
                        ).show()
                        startOnboarding()
                    }
                })

            loadShortcuts()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_entity_grid, container, false)

        viewHolder = ViewHolder(
            root,
            EntityAdapter(
                viewModel::onEntityClick,
                viewModel::onReorderedEntities
            )
        ).apply {
            settingsButton.setOnClickListener {
                startOnboarding()
            }

            editButton.setOnClickListener {
                viewModel.onEditClick()
            }

            with(recyclerView) {
                adapter = itemAdapter
                layoutManager = GridLayoutManager(
                    context,
                    GRID_SPAN_COUNT
                )
                itemTouchHelper.attachToRecyclerView(recyclerView)

                addItemDecoration(
                    GridSpacingItemDecoration(
                        GRID_SPAN_COUNT,
                        resources.getDimensionPixelSize(R.dimen.grid_spacing)
                    )
                )
            }

            setWindowInsets(this)
        }

        return root
    }

    private fun scheduleRefresh() {
        // Only one refresh scheduled at once
        cancelRefresh()

        handler.postDelayed(REFRESH_INTERVAL_MS) {
            viewModel.loadShortcuts()
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
        viewModel.loadShortcuts()
    }

    private val itemTouchHelper by lazy {
        ItemTouchHelper(EditingModeCallback(viewModel))
    }

    private class ViewHolder(val root: View, val itemAdapter: EntityAdapter) {
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView_shortcuts)
        val settingsButton: ImageButton = root.findViewById(R.id.imageButton_settings)
        val editButton: ImageButton = root.findViewById(R.id.imageButton_edit)
        val progress: ProgressBar = root.findViewById(R.id.progress_main)
    }
}

