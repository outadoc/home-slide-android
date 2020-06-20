package fr.outadoc.homeslide.wear.feature.list

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewAnimator
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.wear.activity.ConfirmationActivity
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.github.ajalt.timberkt.Timber
import fr.outadoc.homeslide.common.feature.grid.vm.EntityListViewModel
import fr.outadoc.homeslide.common.feature.grid.vm.EntityListViewModel.Event
import fr.outadoc.homeslide.common.feature.grid.vm.EntityListViewModel.State
import fr.outadoc.homeslide.wear.R
import io.uniflow.androidx.flow.onEvents
import io.uniflow.androidx.flow.onStates
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class EntityListFragment : Fragment() {

    private val vm: EntityListViewModel by viewModel()
    private var viewHolder: ViewHolder? = null

    private val handler: Handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_entity_grid, container, false)

        viewHolder = ViewHolder(root, EntityTileAdapter { entity -> vm.onEntityClick(entity) })

        onStates(vm) { state ->
            when (state) {
                is State.Content -> {
                    viewHolder?.apply {
                        recyclerView.requestFocus()
                        tileAdapter.submitList(state.displayTiles)
                    }
                }
            }

            viewHolder?.apply {
                val childToDisplay = when (state) {
                    is State.Content -> CHILD_CONTENT
                    State.Loading -> CHILD_LOADING
                    else -> CHILD_NO_CONTENT
                }

                if (viewFlipper.displayedChild != childToDisplay) {
                    viewFlipper.displayedChild = childToDisplay
                }

                if (state is State.Content) {
                    recyclerView.requestFocus()
                }
            }
        }

        onEvents(vm) { event ->
            when (event.take()) {
                is Event.Error -> {
                    Intent(requireContext(), ConfirmationActivity::class.java).apply {
                        putExtra(
                            ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                            ConfirmationActivity.FAILURE_ANIMATION
                        )
                        putExtra(
                            ConfirmationActivity.EXTRA_MESSAGE,
                            getString(R.string.list_loading_error)
                        )
                    }.also { intent ->
                        startActivity(intent)
                    }
                }
            }
        }

        scheduleRefresh()
        return root
    }

    override fun onResume() {
        super.onResume()
        vm.loadEntities()
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewHolder = null
    }

    private class ViewHolder(view: View, val tileAdapter: EntityTileAdapter) {
        val viewFlipper: ViewAnimator = view.findViewById(R.id.viewFlipper_entityGrid)
        val recyclerView: WearableRecyclerView =
            view.findViewById<WearableRecyclerView>(R.id.wearableRecyclerView_shortcuts).apply {
                isEdgeItemsCenteringEnabled = true
                layoutManager = WearableLinearLayoutManager(context)
                adapter = tileAdapter
            }
    }

    companion object {
        fun newInstance() = EntityListFragment()

        private const val CHILD_CONTENT = 0
        private const val CHILD_NO_CONTENT = 1
        private const val CHILD_LOADING = 2
    }
}