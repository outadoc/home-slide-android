package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.Action
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.mdi.toIcon

class Climate(state: EntityState) : ABaseEntity(state, "thermostat".toIcon()) {

    companion object {
        const val DOMAIN = "climate"
    }

    override val isOn: Boolean
        get() = stateStr != "off"

    override val primaryAction: Action?
        get() = when (stateStr) {
            "off" -> Action("climate", "turn_on", entityId)
            else -> Action("climate", "turn_off", entityId)
        }
}
