package fr.outadoc.quickhass.wear.feature.list

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.github.ajalt.timberkt.Timber
import fr.outadoc.homeslide.common.feature.grid.vm.EntityGridViewModel
import fr.outadoc.homeslide.common.feature.grid.vm.EntityGridViewModel.GridState
import fr.outadoc.quickhass.wear.R
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class EntityGridFragment : Fragment() {

    private val vm: EntityGridViewModel by viewModel()

    private var viewHolder: ViewHolder? = null

    private val handler: Handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_entity_grid, container, false)

        viewHolder = ViewHolder(root, EntityTileAdapter { entity -> vm.onEntityClick(entity) })

        vm.tiles.observe(viewLifecycleOwner) { tiles ->
            viewHolder?.apply {
                recyclerView.requestFocus()
                tileAdapter.submitList(tiles)
            }
        }

        vm.gridState.observe(viewLifecycleOwner) { state ->
            viewHolder?.apply {
                recyclerView.isVisible = state == GridState.Content
                noContentView.isVisible = state == GridState.NoContent
                loadingView.isVisible = state == GridState.Skeleton
            }
        }

        scheduleRefresh()
        return root
    }

    override fun onResume() {
        super.onResume()
        vm.loadShortcuts()
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewHolder = null
    }

    private class ViewHolder(view: View, val tileAdapter: EntityTileAdapter) {
        val noContentView: View = view.findViewById(R.id.layout_noContent)
        val loadingView: View = view.findViewById(R.id.view_loading)
        val recyclerView: WearableRecyclerView =
            view.findViewById<WearableRecyclerView>(R.id.wearableRecyclerView_shortcuts).apply {
                isEdgeItemsCenteringEnabled = true
                layoutManager = WearableLinearLayoutManager(context)
                adapter = tileAdapter
            }
    }

    companion object {
        fun newInstance() =
            EntityGridFragment()
    }
}