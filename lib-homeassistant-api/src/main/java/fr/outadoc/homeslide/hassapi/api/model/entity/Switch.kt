package fr.outadoc.homeslide.hassapi.api.model.entity

import fr.outadoc.homeslide.hassapi.api.model.EntityState
import fr.outadoc.mdi.toIcon

class Switch(state: EntityState) : ABinaryEntity(state, "power-plug".toIcon()) {

    companion object {
        const val DOMAIN = "switch"
    }
}