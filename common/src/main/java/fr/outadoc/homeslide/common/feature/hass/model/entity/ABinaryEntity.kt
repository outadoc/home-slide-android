package fr.outadoc.homeslide.common.feature.hass.model.entity

import fr.outadoc.homeslide.common.feature.hass.model.Action
import fr.outadoc.homeslide.common.feature.hass.model.EntityState
import fr.outadoc.mdi.FontIcon

abstract class ABinaryEntity(state: EntityState, defaultIcon: FontIcon) :
    ABaseEntity(state, defaultIcon) {

    override val isOn: Boolean = stateStr == "on"

    override val primaryAction = Action(
        "homeassistant", "toggle", entityId
    )
}