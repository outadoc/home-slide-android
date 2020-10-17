/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.outadoc.homeslide.app.feature.slideover.ui

import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
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
import fr.outadoc.homeslide.hassapi.model.entity.base.Entity

class EntityTileAdapter(
    val onItemClickListener: (Entity) -> Unit,
    val onItemLongPressListener: (Entity) -> Boolean,
    val onItemVisibilityChangeListener: (Entity, Boolean) -> Unit,
    val onReorderedListener: (List<Tile<Entity>>) -> Unit,
    val onItemHeightChangeListener: (Int) -> Unit
) : ReorderableListAdapter<Tile<Entity>, EntityTileAdapter.ViewHolder>(TileDiffer()) {

    var isEditingMode = false
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    private var lastMeasuredHeight: Int = 0
        set(value) {
            if (field != value) {
                field = value
                onItemHeightChangeListener(value)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_shortcut, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tile = getItem(position)

        with(holder) {
            if (position == 0) {
                view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        lastMeasuredHeight = view.height
                    }
                })
            }

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
                    view.wiggleWiggle(true)

                    view.setOnClickListener {
                        // Hide / unhide an item
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onItemVisibilityChangeListener(tile.source, tile.isHidden)
                    }

                    view.setOnLongClickListener { false }
                }
                else -> {
                    // When not editing, stop animation, set listeners
                    view.wiggleWiggle(false)

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

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.view.wiggleWiggle(isEditingMode)
    }

    private fun View.wiggleWiggle(wiggle: Boolean) {
        if (wiggle) {
            val wiggleWiggle = AnimationUtils.loadAnimation(context, R.anim.shake)
            startAnimation(wiggleWiggle)
        } else {
            clearAnimation()
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
