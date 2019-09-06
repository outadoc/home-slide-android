package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.FontIcon
import fr.outadoc.quickhass.feature.slideover.model.Action
import fr.outadoc.quickhass.feature.slideover.model.State

abstract class BinaryEntity(state: State, defaultIcon: FontIcon) : Entity(state, defaultIcon) {
    override val isOn: Boolean = stateStr == "on"

    override val primaryAction = Action(
        "homeassistant", "toggle", entityId
    )
}