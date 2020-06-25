package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.mdi.toIcon

class Switch(state: EntityState) : ABinaryEntity(state, "power-plug".toIcon()) {

    companion object {
        const val DOMAIN = "switch"
    }
}
