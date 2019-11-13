package fr.outadoc.quickhass.feature.grid.ui

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class ReorderableListAdapter<T, VH : RecyclerView.ViewHolder>(diffCallback: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, VH>(diffCallback) {

    abstract fun onReordered(list: List<T>)

    fun moveItem(from: Int, to: Int) {
        val items = LinkedList(currentList)

        if (from == to || from !in 0 until items.size || to !in 0 until items.size)
            return

        val itemToMove = items[from]
        items.remove(itemToMove)
        items.add(to, itemToMove)

        submitList(items)
        onReordered(items)
    }
}
