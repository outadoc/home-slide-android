package fr.outadoc.quickhass.feature.grid.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.slideover.model.entity.Entity
import java.util.*

class EntityAdapter(
    val onItemClick: (Entity) -> Unit,
    val onReordered: (List<Entity>) -> Unit,
    val onItemLongPress: (Entity) -> Boolean
) : ReorderableRecyclerViewAdapter<EntityAdapter.ViewHolder>() {

    val items: LinkedList<Entity> = LinkedList()
    var isEditingMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_shortcut, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder) {
            view.alpha = if (isEditingMode) 0.5f else 1.0f

            // Activate the view if the entity is "on"
            view.isActivated = item.isOn

            view.setOnClickListener {
                if (item.isEnabled) {
                    view.isActivated = !view.isActivated
                    onItemClick(item)
                }
            }

            view.setOnLongClickListener {
                onItemLongPress(item)
            }

            label.text = item.friendlyName ?: item.entityId

            icon.visibility = if (item.formattedState == null) View.VISIBLE else View.INVISIBLE
            state.visibility = if (item.formattedState != null) View.VISIBLE else View.INVISIBLE

            if (item.formattedState == null) {
                icon.text = item.icon.unicodePoint
            } else {
                state.text = item.formattedState
            }
        }
    }

    override fun moveItem(from: Int, to: Int) {
        if (from == to || from !in 0 until items.size || to !in 0 until items.size)
            return

        val itemToMove = items[from]
        items.remove(itemToMove)
        items.add(to, itemToMove)

        onReordered(items)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.tv_shortcut_label)
        val icon: TextView = view.findViewById(R.id.tv_shortcut_icon)
        val state: TextView = view.findViewById(R.id.tv_extra_state)
    }
}