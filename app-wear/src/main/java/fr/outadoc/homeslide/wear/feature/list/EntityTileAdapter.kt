package fr.outadoc.homeslide.wear.feature.list

import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.homeslide.common.feature.grid.ui.TileDiffer
import fr.outadoc.homeslide.hassapi.model.Tile
import fr.outadoc.homeslide.hassapi.model.entity.Entity
import fr.outadoc.homeslide.wear.R

class EntityTileAdapter(
    val onItemClickListener: (Entity) -> Unit
) : ListAdapter<Tile<Entity>, EntityTileAdapter.ViewHolder>(TileDiffer()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_shortcut, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tile = getItem(position)

        with(holder) {
            // Activate the view if the entity is "on"
            view.isActivated = tile.isActivated

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

            actionContainer.setOnClickListener {
                actionContainer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

                if (tile.isToggleable) {
                    view.isActivated = !view.isActivated
                    onItemClickListener(tile.source)
                }
            }
        }
    }

    private fun TextView.setTextOrInvisible(text: String?) {
        isInvisible = text == null
        this.text = text
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.tv_shortcut_label)
        val actionContainer: View = view.findViewById(R.id.frameLayout_actionContainer)
        val icon: TextView = view.findViewById(R.id.tv_shortcut_icon)
        val state: TextView = view.findViewById(R.id.tv_extra_state)
        val loadingIndicator: ProgressBar = view.findViewById(R.id.pb_shortcut_loading)
    }
}