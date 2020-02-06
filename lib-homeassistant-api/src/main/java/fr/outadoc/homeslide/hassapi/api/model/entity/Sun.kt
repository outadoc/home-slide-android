package fr.outadoc.homeslide.hassapi.api.model.entity

import fr.outadoc.homeslide.hassapi.api.model.EntityState
import fr.outadoc.mdi.toIcon

class Sun(state: EntityState) : ABaseEntity(state, "weather-sunny".toIcon()) {

    companion object {
        const val DOMAIN = "sun"
    }
}