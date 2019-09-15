package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.FontIcon
import fr.outadoc.quickhass.feature.slideover.model.Action
import fr.outadoc.quickhass.feature.slideover.model.EntityState

abstract class BinaryEntity(state: EntityState, defaultIcon: FontIcon) : Entity(state, defaultIcon) {

    override val isOn: Boolean = stateStr == "on"

    override val primaryAction = Action(
        "homeassistant", "toggle", entityId
    )
}