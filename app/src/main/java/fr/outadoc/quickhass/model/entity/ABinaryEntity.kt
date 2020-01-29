package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.FontIcon
import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.model.EntityState

abstract class ABinaryEntity(state: EntityState, defaultIcon: FontIcon) :
    ABaseEntity(state, defaultIcon) {

    override val isOn: Boolean = stateStr == "on"

    override val primaryAction = Action(
        "homeassistant", "toggle", entityId
    )
}