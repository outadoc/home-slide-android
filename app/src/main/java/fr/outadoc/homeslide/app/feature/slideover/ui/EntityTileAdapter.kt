package fr.outadoc.homeslide.app.feature.slideover.ui

import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.app.feature.grid.ui.ReorderableListAdapter
import fr.outadoc.homeslide.common.feature.grid.ui.TileDiffer
import fr.outadoc.homeslide.hassapi.model.Tile
import fr.outadoc.homeslide.hassapi.model.entity.Entity

class EntityTileAdapter(
    val onItemClickListener: (Entity) -> Unit,
    val onItemLongPressListener: (Entity) -> Boolean,
    val onItemVisibilityChangeListener: (Entity, Boolean) -> Unit,
    val onReorderedListener: (List<Tile<Entity>>) -> Unit
) : ReorderableListAdapter<Tile<Entity>, EntityTileAdapter.ViewHolder>(TileDiffer()) {

    var isEditingMode = false

    private var wiggleWiggle: Animation? = null

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

            // Display "hidden" overlay if relevant
            overlayHidden.isVisible = tile.isHidden

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

                    view.setOnClickListener {
                        // Hide / unhide an item
                        onItemVisibilityChangeListener(tile.source, tile.isHidden)
                    }
                    view.setOnLongClickListener { false }
                }
                else -> {
                    // When not editing, stop animation, set listeners
                    view.clearAnimation()

                    view.setOnClickListener {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

                        if (tile.isToggleable) {
                            view.isActivated = !view.isActivated
                            onItemClickListener(tile.source)
                        }
                    }

                    view.setOnLongClickListener {
                        onItemLongPressListener(tile.source)
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
        onReorderedListener(list)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.tv_shortcut_label)
        val icon: TextView = view.findViewById(R.id.tv_shortcut_icon)
        val state: TextView = view.findViewById(R.id.tv_extra_state)
        val loadingIndicator: ProgressBar = view.findViewById(R.id.pb_shortcut_loading)
        val overlayHidden: FrameLayout = view.findViewById(R.id.frameLayout_overlay_hidden)
    }
}