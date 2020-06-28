package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.base.ToggleableEntity
import fr.outadoc.mdi.toIcon

class MediaPlayer(state: EntityState) : ToggleableEntity(state, "cast".toIcon()) {

    companion object {
        const val DOMAIN = "media_player"
    }
}
