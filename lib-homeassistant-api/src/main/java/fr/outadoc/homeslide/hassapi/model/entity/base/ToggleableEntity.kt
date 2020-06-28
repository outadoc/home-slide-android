package fr.outadoc.homeslide.hassapi.model.entity.base

import fr.outadoc.homeslide.hassapi.model.Action
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.mdi.FontIcon

abstract class ToggleableEntity(state: EntityState, defaultIcon: FontIcon) :
    BaseEntity(state, defaultIcon) {

    override val isOn: Boolean = stateStr == "on"

    override val primaryAction = Action(
        "homeassistant", "toggle", entityId
    )
}
