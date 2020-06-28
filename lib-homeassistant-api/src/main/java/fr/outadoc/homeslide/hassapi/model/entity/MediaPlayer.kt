package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.Action
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.base.ToggleableEntity
import fr.outadoc.mdi.FontIcon
import fr.outadoc.mdi.toIcon

class MediaPlayer(state: EntityState) : ToggleableEntity(state, "cast".toIcon()) {

    companion object {
        const val DOMAIN = "media_player"

        private const val STATE_PLAYING = "playing"
        private const val STATE_UNAVAILABLE = "unavailable"
    }

    override val isOn: Boolean = stateStr == STATE_PLAYING

    override val primaryAction =
        Action(DOMAIN, "media_play_pause", entityId)

    override val fallbackIcon: FontIcon?
        get() = when (stateStr) {
            STATE_PLAYING -> "cast-connected".toIcon()
            STATE_UNAVAILABLE -> "cast-off".toIcon()
            else -> null
        }
}
