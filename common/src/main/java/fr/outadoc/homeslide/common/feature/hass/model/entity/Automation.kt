package fr.outadoc.homeslide.common.feature.hass.model.entity

import fr.outadoc.homeslide.common.feature.hass.model.EntityState
import fr.outadoc.mdi.toIcon

class Automation(state: EntityState) : ABaseEntity(state, "playlist-play".toIcon()) {

    companion object {
        const val DOMAIN = "automation"
    }
}