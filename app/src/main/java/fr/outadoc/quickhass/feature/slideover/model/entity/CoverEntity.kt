package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.feature.slideover.model.Action
import fr.outadoc.quickhass.feature.slideover.model.EntityState

class CoverEntity(state: EntityState) : Entity(state, "window-open".toIcon()!!) {
    companion object {
        const val DOMAIN = "cover"
    }

    override val isOn: Boolean = stateStr == "open"

    override val primaryAction: Action?
        get() = when (stateStr) {
            "open" -> Action("cover", "close_cover", entityId)
            "closed" -> Action("cover", "open_cover", entityId)
            else -> null
        }
}