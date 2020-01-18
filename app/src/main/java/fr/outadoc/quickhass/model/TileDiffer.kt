package fr.outadoc.quickhass.model

import androidx.recyclerview.widget.DiffUtil
import fr.outadoc.quickhass.feature.slideover.model.Tile

class TileDiffer<T> : DiffUtil.ItemCallback<Tile<T>>() {

    override fun areItemsTheSame(oldItem: Tile<T>, newItem: Tile<T>): Boolean {
        return oldItem.source == newItem.source
    }

    override fun areContentsTheSame(oldItem: Tile<T>, newItem: Tile<T>): Boolean {
        return oldItem == newItem
    }
}