package fr.outadoc.homeslide.wear.feature.list

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.wear.activity.ConfirmationActivity
import androidx.wear.widget.WearableLinearLayoutManager
import fr.outadoc.homeslide.common.feature.grid.vm.EntityListViewModel
import fr.outadoc.homeslide.common.feature.grid.vm.EntityListViewModel.Event
import fr.outadoc.homeslide.common.feature.grid.vm.EntityListViewModel.State
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.wear.R
import fr.outadoc.homeslide.wear.databinding.FragmentEntityListBinding
import io.uniflow.androidx.flow.onEvents
import io.uniflow.androidx.flow.onStates
import java.util.concurrent.TimeUnit
import org.koin.android.viewmodel.ext.android.viewModel

class EntityListFragment : Fragment() {

    private val vm: EntityListViewModel by viewModel()

    private var binding: FragmentEntityListBinding? = null
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val tileAdapter = EntityTileAdapter { entity -> vm.onEntityClick(entity) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEntityListBinding.inflate(inflater, container, false)

        binding?.wearableRecyclerViewShortcuts?.apply {
            isEdgeItemsCenteringEnabled = true
            layoutManager = WearableLinearLayoutManager(context)
            adapter = tileAdapter
        }

        onStates(vm) { state ->
            when (state) {
                is State.Content -> {
                    binding?.apply {
                        wearableRecyclerViewShortcuts.requestFocus()
                        tileAdapter.submitList(state.displayTiles)
                    }
                }
            }

            binding?.apply {
                val childToDisplay = when (state) {
                    is State.Content -> CHILD_CONTENT
                    State.Loading -> CHILD_LOADING
                    else -> CHILD_NO_CONTENT
                }

                if (viewFlipperEntityList.displayedChild != childToDisplay) {
                    viewFlipperEntityList.displayedChild = childToDisplay
                }

                if (state is State.Content) {
                    wearableRecyclerViewShortcuts.requestFocus()
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
        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        vm.loadEntities()
    }

    private fun scheduleRefresh() {
        // Only one refresh scheduled at once
        cancelRefresh()

        KLog.d { "scheduling refresh" }
        handler.postDelayed(TimeUnit.SECONDS.toMillis(vm.refreshIntervalSeconds)) {
            vm.loadEntities()
        }
    }

    private fun cancelRefresh() {
        KLog.d { "canceling refresh" }
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        fun newInstance() = EntityListFragment()

        private const val CHILD_CONTENT = 0
        private const val CHILD_NO_CONTENT = 1
        private const val CHILD_LOADING = 2
    }
}
