package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.FontIcon
import fr.outadoc.mdi.toIcon
import fr.outadoc.mdi.toIconOrNull
import fr.outadoc.quickhass.model.EntityState

class PersonEntity(state: EntityState) : BaseEntity(state, "account".toIcon()) {

    companion object {
        const val DOMAIN = "person"
    }

    override val fallbackIcon: FontIcon?
        get() = when (stateStr) {
            "home" -> "account-check"
            else -> "account-remove-outline"
        }.toIconOrNull()
}