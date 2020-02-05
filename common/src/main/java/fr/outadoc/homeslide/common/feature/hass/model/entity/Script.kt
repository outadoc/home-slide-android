package fr.outadoc.homeslide.common.feature.hass.model.entity

import fr.outadoc.homeslide.common.feature.hass.model.Action
import fr.outadoc.homeslide.common.feature.hass.model.EntityState
import fr.outadoc.mdi.toIcon

class Script(state: EntityState) : ABaseEntity(state, "file-document".toIcon()) {

    companion object {
        const val DOMAIN = "script"
    }

    private val scriptName = entityId.takeLastWhile { it != '.' }

    override val primaryAction: Action?
        get() = Action("script", scriptName, entityId)
}