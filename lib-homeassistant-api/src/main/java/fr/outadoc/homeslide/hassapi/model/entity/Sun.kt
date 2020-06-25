package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.mdi.toIcon

class Sun(state: EntityState) : ABaseEntity(state, "weather-sunny".toIcon()) {

    companion object {
        const val DOMAIN = "sun"
    }
}
