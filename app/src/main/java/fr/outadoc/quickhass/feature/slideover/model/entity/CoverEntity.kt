package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.feature.slideover.model.Action
import fr.outadoc.quickhass.feature.slideover.model.State

class CoverEntity(state: State) : Entity(state, "window-open".toIcon()!!) {
    override val isOn: Boolean = stateStr == "open"

    override val primaryAction: Action?
        get() = when (stateStr) {
            "open" -> Action("cover", "close_cover", entityId)
            "closed" -> Action("cover", "open_cover", entityId)
            else -> null
        }
}