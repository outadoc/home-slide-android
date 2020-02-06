package fr.outadoc.homeslide.hassapi.api.model.entity

import fr.outadoc.homeslide.hassapi.api.model.EntityState
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