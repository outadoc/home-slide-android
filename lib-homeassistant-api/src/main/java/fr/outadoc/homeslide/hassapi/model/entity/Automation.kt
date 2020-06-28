package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.base.BaseEntity
import fr.outadoc.mdi.toIcon

class Automation(state: EntityState) : BaseEntity(state, "playlist-play".toIcon()) {

    companion object {
        const val DOMAIN = "automation"
    }
}
