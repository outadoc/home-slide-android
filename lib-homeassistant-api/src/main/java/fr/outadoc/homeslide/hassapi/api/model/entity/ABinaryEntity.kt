package fr.outadoc.homeslide.hassapi.api.model.entity

import fr.outadoc.homeslide.hassapi.api.model.Action
import fr.outadoc.homeslide.hassapi.api.model.EntityState
import fr.outadoc.mdi.FontIcon

abstract class ABinaryEntity(state: EntityState, defaultIcon: FontIcon) :
    ABaseEntity(state, defaultIcon) {

    override val isOn: Boolean = stateStr == "on"

    override val primaryAction = Action(
        "homeassistant", "toggle", entityId
    )
}