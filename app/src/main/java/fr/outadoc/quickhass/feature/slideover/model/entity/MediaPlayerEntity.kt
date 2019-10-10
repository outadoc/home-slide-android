package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.feature.slideover.model.EntityState

class MediaPlayerEntity(state: EntityState) : BinaryEntity(state, "cast".toIcon()) {

    companion object {
        const val DOMAIN = "media_player"
    }
}