package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.model.EntityState

class ClimateEntity(state: EntityState) : BaseEntity(state, "thermostat".toIcon()) {

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