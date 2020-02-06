package fr.outadoc.homeslide.hassapi.api.model.entity

import fr.outadoc.homeslide.hassapi.api.model.EntityState
import fr.outadoc.mdi.toIcon

class InputBoolean(state: EntityState) : ABinaryEntity(state, "dip-switch".toIcon()) {

    companion object {
        const val DOMAIN = "input_boolean"
    }
}