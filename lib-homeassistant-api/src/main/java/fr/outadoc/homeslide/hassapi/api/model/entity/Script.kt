package fr.outadoc.homeslide.hassapi.api.model.entity

import fr.outadoc.homeslide.hassapi.api.model.Action
import fr.outadoc.homeslide.hassapi.api.model.EntityState
import fr.outadoc.mdi.toIcon

class Script(state: EntityState) : ABaseEntity(state, "file-document".toIcon()) {

    companion object {
        const val DOMAIN = "script"
    }

    private val scriptName = entityId.takeLastWhile { it != '.' }

    override val primaryAction: Action?
        get() = Action("script", scriptName, entityId)
}