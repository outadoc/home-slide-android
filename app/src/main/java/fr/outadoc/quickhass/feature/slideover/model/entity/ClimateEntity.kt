package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.feature.slideover.model.Action
import fr.outadoc.quickhass.feature.slideover.model.State

class ClimateEntity(state: State) : Entity(state, "thermostat".toIcon()!!) {
    override val isOn: Boolean
        get() = stateStr != "off"

    override val primaryAction: Action?
        get() = when (stateStr) {
            "off" -> Action("climate", "turn_on", entityId)
            else -> Action("climate", "turn_off", entityId)
        }
}