package fr.outadoc.homeslide.common.feature.hass.model.entity

import fr.outadoc.homeslide.common.feature.hass.model.EntityState
import fr.outadoc.mdi.toIcon

class InputBoolean(state: EntityState) : ABinaryEntity(state, "dip-switch".toIcon()) {

    companion object {
        const val DOMAIN = "input_boolean"
    }
}