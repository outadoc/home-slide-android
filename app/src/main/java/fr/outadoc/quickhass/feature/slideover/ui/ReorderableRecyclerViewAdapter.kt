package fr.outadoc.quickhass.feature.slideover.ui

import androidx.recyclerview.widget.RecyclerView

abstract class ReorderableRecyclerViewAdapter<VH : RecyclerView.ViewHolder> :
    RecyclerView.Adapter<VH>() {

    abstract fun moveItem(from: Int, to: Int)
}
