package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.mdi.toIcon

class Automation(state: EntityState) : ABaseEntity(state, "playlist-play".toIcon()) {

    companion object {
        const val DOMAIN = "automation"
    }
}