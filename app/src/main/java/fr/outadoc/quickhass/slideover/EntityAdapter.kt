package fr.outadoc.quickhass.slideover

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.extensions.swap
import fr.outadoc.quickhass.model.Entity

class EntityAdapter(
    val onItemClick: (Entity) -> Unit,
    val onReordered: (List<Entity>) -> Unit
) : ReorderableRecyclerViewAdapter<EntityAdapter.ViewHolder>() {
    val items: MutableList<Entity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_shortcut, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder) {
            // Activate the view if the entity is "on"
            view.isActivated = item.isOn

            view.setOnClickListener {
                if (item.isEnabled) {
                    view.isActivated = !view.isActivated
                    onItemClick(item)
                }
            }

            label.text = item.friendlyName ?: item.entityId
            icon.text = item.icon.unicodePoint
        }
    }

    override fun moveItem(from: Int, to: Int) {
        items.swap(from, to)
        onReordered(items)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.tv_shortcut_label)
        val icon: TextView = view.findViewById(R.id.tv_shortcut_icon)
    }
}