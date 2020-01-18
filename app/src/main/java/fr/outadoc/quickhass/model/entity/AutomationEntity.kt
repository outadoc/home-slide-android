package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.model.EntityState

class AutomationEntity(state: EntityState) : BaseEntity(state, "playlist-play".toIcon()) {

    companion object {
        const val DOMAIN = "automation"
    }
}