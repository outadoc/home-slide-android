package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.model.EntityState

class Sun(state: EntityState) : ABaseEntity(state, "weather-sunny".toIcon()) {

    companion object {
        const val DOMAIN = "sun"
    }
}