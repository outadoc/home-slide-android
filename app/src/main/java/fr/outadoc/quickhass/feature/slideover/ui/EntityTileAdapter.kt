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
import fr.outadoc.quickhass.feature.grid.ui.ReorderableListAdapter
import fr.outadoc.quickhass.feature.slideover.model.Tile
import fr.outadoc.quickhass.model.TileDiffer
import fr.outadoc.quickhass.model.entity.Entity

class EntityTileAdapter(
    val onItemClickListener: (Entity) -> Unit,
    val onReorderedListener: (List<Entity>) -> Unit,
    val onItemLongPress: (Entity) -> Boolean
) : ReorderableListAdapter<Tile<Entity>, EntityTileAdapter.ViewHolder>(TileDiffer()) {

    var isEditingMode = false

    private var wiggleWiggle: Animation? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shortcut, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tile = getItem(position)

        with(holder) {
            // Activate the view if the entity is "on"
            view.isActivated = tile.isActivated

            // Disable when editing
            view.isEnabled = !isEditingMode

            // Set label
            label.text = tile.label

            // Set icon or state label
            icon.setTextOrInvisible(tile.icon)
            state.setTextOrInvisible(tile.state)

            // Display loading indicator if relevant
            loadingIndicator.isVisible = tile.isLoading

            if (tile.isLoading) {
                icon.isInvisible = true
                state.isInvisible = true
            }

            when {
                isEditingMode -> {
                    // When editing, start animation, clear listeners
                    if (wiggleWiggle == null) {
                        wiggleWiggle = AnimationUtils.loadAnimation(view.context, R.anim.shake)
                    }

                    view.startAnimation(wiggleWiggle)

                    view.setOnClickListener { }
                    view.setOnLongClickListener { false }
                }
                else -> {
                    // When not editing, stop animation, set listeners
                    view.clearAnimation()

                    view.setOnClickListener {
                        view.isActivated = !view.isActivated
                        onItemClickListener(tile.source)
                    }

                    view.setOnLongClickListener {
                        onItemLongPress(tile.source)
                    }
                }
            }
        }
    }

    private fun TextView.setTextOrInvisible(text: String?) {
        isInvisible = text == null
        this.text = text
    }

    override fun onReordered(list: List<Tile<Entity>>) {
        onReorderedListener(list.map { it.source })
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.tv_shortcut_label)
        val icon: TextView = view.findViewById(R.id.tv_shortcut_icon)
        val state: TextView = view.findViewById(R.id.tv_extra_state)
        val loadingIndicator: ProgressBar = view.findViewById(R.id.pb_shortcut_loading)
    }
}