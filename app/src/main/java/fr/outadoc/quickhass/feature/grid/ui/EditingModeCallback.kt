package fr.outadoc.quickhass.feature.grid.ui

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.quickhass.feature.grid.vm.EntityGridViewModel

class EditingModeCallback(private val viewModel: EntityGridViewModel) :
    SimpleCallback(UP or DOWN or START or END, 0) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val adapter = recyclerView.adapter as ReorderableListAdapter<*, *>

        val from = viewHolder.adapterPosition
        val to = target.adapterPosition

        adapter.moveItem(from, to)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (actionState == ACTION_STATE_DRAG) {
            viewHolder?.itemView?.scale(1.1f, 10.0f)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.scale(1.0f, 0.0f)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return viewModel.editionState.value == EntityGridViewModel.EditionState.Editing
    }

    private fun View.scale(scale: Float, elevation: Float) {
        this.animate()
            .translationZ(elevation)
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(50)
            .start()
    }
}