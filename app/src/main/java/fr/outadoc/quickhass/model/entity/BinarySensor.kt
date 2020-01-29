package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.FontIcon
import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.model.EntityState

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