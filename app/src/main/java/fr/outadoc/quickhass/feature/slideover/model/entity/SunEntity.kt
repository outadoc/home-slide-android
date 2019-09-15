package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.feature.slideover.model.EntityState

class SunEntity(state: EntityState) : Entity(state, "weather-sunny".toIcon()!!) {

    companion object {
        const val DOMAIN = "sun"
    }
}