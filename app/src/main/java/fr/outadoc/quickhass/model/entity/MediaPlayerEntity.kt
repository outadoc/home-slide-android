package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.model.EntityState

class MediaPlayerEntity(state: EntityState) : BinaryEntity(state, "cast".toIcon()) {

    companion object {
        const val DOMAIN = "media_player"
    }
}