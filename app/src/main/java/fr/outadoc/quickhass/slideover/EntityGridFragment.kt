package fr.outadoc.quickhass.slideover

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
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.preferences.MainActivity


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
            shortcuts.observe(this@EntityGridFragment, Observer { shortcuts ->
                viewHolder?.itemAdapter?.apply {
                    items.clear()
                    items.addAll(shortcuts)
                    notifyDataSetChanged()
                }

                scheduleRefresh()
            })

            error.observe(this@EntityGridFragment, Observer { e ->
                Toast.makeText(
                    context,
                    e.message ?: getString(R.string.toast_generic_error_title),
                    Toast.LENGTH_SHORT
                ).show()

                scheduleRefresh()
            })

            isLoading.observe(this@EntityGridFragment, Observer { isLoading ->
                viewHolder?.apply {
                    progress.isVisible = isLoading
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
                        openSettings()
                    }
                })

            startLoading()
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
            EntityAdapter(viewModel::onEntityClick, viewModel::onReorderedEntities)
        ).apply {
            settingsButton.setOnClickListener {
                openSettings()
            }

            with(recyclerView) {
                adapter = itemAdapter
                layoutManager = GridLayoutManager(context, GRID_SPAN_COUNT)
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
        handler.postDelayed(REFRESH_INTERVAL_MS) {
            viewModel.loadShortcuts()
        }
    }

    private fun cancelRefresh() {
        handler.removeCallbacksAndMessages(null)
    }

    private fun openSettings() {
        Intent(activity, MainActivity::class.java).let { i ->
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

        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or
                        ItemTouchHelper.DOWN or
                        ItemTouchHelper.START or
                        ItemTouchHelper.END, 0
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val adapter = recyclerView.adapter as ReorderableRecyclerViewAdapter

                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition

                    adapter.moveItem(from = viewHolder.adapterPosition, to = target.adapterPosition)
                    adapter.notifyItemMoved(from, to)

                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                        cancelRefresh()
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1.0f
                    scheduleRefresh()
                }
            }

        ItemTouchHelper(simpleItemTouchCallback)
    }

    private class ViewHolder(val root: View, val itemAdapter: EntityAdapter) {
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView_shortcuts)
        val settingsButton: ImageButton = root.findViewById(R.id.imageButton_settings)
        val progress: ProgressBar = root.findViewById(R.id.progress_main)
    }
}