package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.Action
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.mdi.toIcon

class Vacuum(state: EntityState) : ABaseEntity(state, "robot-vacuum".toIcon()) {

    companion object {
        const val DOMAIN = "vacuum"
    }

    override val isOn: Boolean
        get() = state.state == "cleaning"

    override val primaryAction: Action?
        get() = when (stateStr) {
            "cleaning" -> Action(DOMAIN, "pause", entityId)
            "docked" -> Action(DOMAIN, "start", entityId)
            else -> null
        }
}