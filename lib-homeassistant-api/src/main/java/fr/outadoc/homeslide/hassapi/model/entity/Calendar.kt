package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.base.ToggleableEntity
import fr.outadoc.mdi.FontIcon
import fr.outadoc.mdi.toIcon

class Calendar(state: EntityState) : ToggleableEntity(state, "calendar-blank-outline".toIcon()) {

    companion object {
        const val DOMAIN = "calendar"
    }

    override val fallbackIcon: FontIcon?
        get() = when (stateStr) {
            "on" -> "calendar-check-outline".toIcon()
            else -> null
        }
}