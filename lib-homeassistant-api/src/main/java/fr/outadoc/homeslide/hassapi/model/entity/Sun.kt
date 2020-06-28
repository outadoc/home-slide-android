package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.base.BaseEntity
import fr.outadoc.mdi.toIcon

class Sun(state: EntityState) : BaseEntity(state, "weather-sunny".toIcon()) {

    companion object {
        const val DOMAIN = "sun"
    }
}
