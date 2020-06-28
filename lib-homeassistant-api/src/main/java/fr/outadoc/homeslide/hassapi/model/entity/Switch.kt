package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.base.ToggleableEntity
import fr.outadoc.mdi.toIcon

class Switch(state: EntityState) : ToggleableEntity(state, "power-plug".toIcon()) {

    companion object {
        const val DOMAIN = "switch"
    }
}
