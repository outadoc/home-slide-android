package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.mdi.FontIcon
import fr.outadoc.mdi.toIcon

class BinarySensor(state: EntityState) : ABaseEntity(state, "eye".toIcon()) {

    companion object {
        const val DOMAIN = "binary_sensor"
    }

    override val fallbackIcon: FontIcon?
        get() = when (state.state) {
            "on" -> "eye-check"
            else -> "eye-outline"
        }.toIcon()
}