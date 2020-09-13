package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.base.ToggleableEntity
import fr.outadoc.mdi.toIcon

class Fan(state: EntityState) : ToggleableEntity(state, "fan".toIcon()) {

    companion object {
        const val DOMAIN = "fan"
    }
}
