package fr.outadoc.homeslide.hassapi.api.model.entity

import fr.outadoc.homeslide.hassapi.api.model.EntityState
import fr.outadoc.mdi.toIcon

class Group(state: EntityState) : ABaseEntity(state, "google-circles-communities".toIcon()) {

    companion object {
        const val DOMAIN = "group"
    }
}