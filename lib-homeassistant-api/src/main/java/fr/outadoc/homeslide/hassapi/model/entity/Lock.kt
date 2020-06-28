package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.Action
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.base.BaseEntity
import fr.outadoc.mdi.FontIcon
import fr.outadoc.mdi.toIcon

class Lock(state: EntityState) : BaseEntity(state, "lock-question".toIcon()) {

    companion object {
        const val DOMAIN = "lock"

        private const val STATE_LOCKED = "locked"
        private const val STATE_UNLOCKED = "unlocked"
    }

    override val isOn: Boolean = stateStr == STATE_UNLOCKED

    override val primaryAction: Action?
        get() = when (stateStr) {
            STATE_LOCKED -> Action(DOMAIN, "unlock", entityId)
            STATE_UNLOCKED -> Action(DOMAIN, "lock", entityId)
            else -> null
        }

    override val fallbackIcon: FontIcon?
        get() = when (stateStr) {
            STATE_LOCKED -> "lock-outline".toIcon()
            STATE_UNLOCKED -> "lock-open-variant-outline".toIcon()
            else -> null
        }
}
