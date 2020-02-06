package fr.outadoc.homeslide.hassapi.api.model.entity

import fr.outadoc.homeslide.hassapi.api.model.EntityState
import fr.outadoc.mdi.toIcon

class MediaPlayer(state: EntityState) : ABinaryEntity(state, "cast".toIcon()) {

    companion object {
        const val DOMAIN = "media_player"
    }
}