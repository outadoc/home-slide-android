package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.model.EntityState

class SunEntity(state: EntityState) : BaseEntity(state, "weather-sunny".toIcon()) {

    companion object {
        const val DOMAIN = "sun"
    }
}