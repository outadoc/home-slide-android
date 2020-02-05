package fr.outadoc.homeslide.common.feature.hass.model.entity

import fr.outadoc.homeslide.common.feature.hass.model.EntityState
import fr.outadoc.mdi.FontIcon
import fr.outadoc.mdi.toIcon
import fr.outadoc.mdi.toIconOrNull

class Person(state: EntityState) : ABaseEntity(state, "account".toIcon()) {

    companion object {
        const val DOMAIN = "person"
    }

    override val fallbackIcon: FontIcon?
        get() = when (stateStr) {
            "home" -> "account-check"
            else -> "account-remove-outline"
        }.toIconOrNull()
}