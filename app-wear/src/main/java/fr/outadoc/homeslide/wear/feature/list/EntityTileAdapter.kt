package fr.outadoc.homeslide.wear.feature.list

import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.homeslide.common.feature.grid.ui.TileDiffer
import fr.outadoc.homeslide.hassapi.model.Tile
import fr.outadoc.homeslide.hassapi.model.entity.base.Entity
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

            state.text = null
            icon.text = null

            when {
                tile.isLoading -> {
                    viewFlipper.displayedChild = CHILD_LOADING
                }
                tile.state != null -> {
                    state.text = tile.state
                    viewFlipper.displayedChild = CHILD_STATE
                }
                tile.icon != null -> {
                    // Set icon or state label
                    icon.text = tile.icon
                    viewFlipper.displayedChild = CHILD_ICON
                }
            }

            view.setOnClickListener {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

                if (tile.isToggleable) {
                    view.isActivated = !view.isActivated
                    onItemClickListener(tile.source)
                }
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.tv_shortcut_label)
        val icon: TextView = view.findViewById(R.id.tv_shortcut_icon)
        val state: TextView = view.findViewById(R.id.tv_extra_state)
        val viewFlipper: ViewFlipper = view.findViewById(R.id.viewFlipper_actionContainer)
    }

    companion object {
        private const val CHILD_ICON = 0
        private const val CHILD_STATE = 1
        private const val CHILD_LOADING = 2
    }
}
