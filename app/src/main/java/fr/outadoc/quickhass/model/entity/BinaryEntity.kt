package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.FontIcon
import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.model.EntityState

abstract class BinaryEntity(state: EntityState, defaultIcon: FontIcon) : BaseEntity(state, defaultIcon) {

    override val isOn: Boolean = stateStr == "on"

    override val primaryAction = Action(
        "homeassistant", "toggle", entityId
    )
}