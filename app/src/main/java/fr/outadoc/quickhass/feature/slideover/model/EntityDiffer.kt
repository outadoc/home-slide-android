package fr.outadoc.quickhass.feature.slideover.model

import androidx.recyclerview.widget.DiffUtil
import fr.outadoc.quickhass.feature.slideover.model.entity.Entity

object EntityDiffer : DiffUtil.ItemCallback<Entity>() {

    override fun areItemsTheSame(oldItem: Entity, newItem: Entity): Boolean {
        return oldItem.entityId == newItem.entityId
    }

    override fun areContentsTheSame(oldItem: Entity, newItem: Entity): Boolean {
        return oldItem.friendlyName == newItem.friendlyName
                && oldItem.icon == newItem.icon
                && oldItem.isOn == newItem.isOn
                && oldItem.isEnabled == newItem.isEnabled
                && oldItem.isVisible == newItem.isVisible
    }
}