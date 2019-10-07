package fr.outadoc.quickhass.feature.slideover.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.grid.ui.ReorderableRecyclerViewAdapter
import fr.outadoc.quickhass.feature.slideover.model.annotation.StringEntityId
import fr.outadoc.quickhass.feature.slideover.model.entity.Entity
import java.util.*
import kotlin.properties.Delegates

class EntityAdapter(
    val onItemClick: (Entity) -> Unit,
    val onReordered: (List<Entity>) -> Unit,
    val onItemLongPress: (Entity) -> Boolean
) : ReorderableRecyclerViewAdapter<EntityAdapter.ViewHolder>() {

    val items: LinkedList<Entity> = LinkedList()
    var isEditingMode = false

    private var wiggleWiggle: Animation? = null

    var loadingEntityIds: Set<@StringEntityId String> by Delegates.observable(emptySet()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shortcut, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder) {
            // Activate the view if the entity is "on"
            view.isActivated = item.isOn

            if (isEditingMode) {
                // When editing, start animation, clear listeners
                if (wiggleWiggle == null) {
                    wiggleWiggle = AnimationUtils.loadAnimation(view.context, R.anim.shake)
                }

                view.startAnimation(wiggleWiggle)

                view.setOnClickListener { }
                view.setOnLongClickListener { false }
            }

            if (!isEditingMode) {
                // When not editing, stop animation, set listeners
                view.clearAnimation()

                view.setOnClickListener {
                    if (item.isEnabled) {
                        view.isActivated = !view.isActivated
                        onItemClick(item)
                    }
                }

                view.setOnLongClickListener {
                    onItemLongPress(item)
                }
            }

            // Disable when editing
            view.isEnabled = !isEditingMode

            // Set label
            label.text = item.friendlyName ?: item.entityId

            // Set icon or state label
            if (item.formattedState == null) {
                icon.text = item.icon.unicodePoint
            } else {
                state.text = item.formattedState
            }

            // Display loading indicator if relevant
            val isLoading = item.entityId in loadingEntityIds
            loadingIndicator.isVisible = isLoading

            if (isLoading) {
                icon.isInvisible = true
                state.isInvisible = true
            } else {
                icon.isInvisible = item.formattedState != null
                state.isInvisible = item.formattedState == null
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
        val loadingIndicator: ProgressBar = view.findViewById(R.id.pb_shortcut_loading)
    }
}