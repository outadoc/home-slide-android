package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.feature.slideover.model.EntityState

class AutomationEntity(state: EntityState) : Entity(state, "playlist-play".toIcon()!!) {

    companion object {
        const val DOMAIN = "automation"
    }
}