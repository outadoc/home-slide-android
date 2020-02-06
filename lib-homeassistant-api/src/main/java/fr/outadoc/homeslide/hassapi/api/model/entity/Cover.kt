package fr.outadoc.homeslide.hassapi.api.model.entity

import fr.outadoc.homeslide.hassapi.api.model.Action
import fr.outadoc.homeslide.hassapi.api.model.EntityState
import fr.outadoc.mdi.toIcon

class Cover(state: EntityState) : ABaseEntity(state, "window-open".toIcon()) {

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