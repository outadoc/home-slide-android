package fr.outadoc.homeslide.common.feature.hass.model.entity

import fr.outadoc.homeslide.common.feature.hass.model.EntityState
import fr.outadoc.mdi.toIcon

class MediaPlayer(state: EntityState) : ABinaryEntity(state, "cast".toIcon()) {

    companion object {
        const val DOMAIN = "media_player"
    }
}