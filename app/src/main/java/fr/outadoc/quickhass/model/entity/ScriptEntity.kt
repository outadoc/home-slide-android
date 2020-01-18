package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.model.EntityState

class ScriptEntity(state: EntityState) : BaseEntity(state, "file-document".toIcon()) {

    companion object {
        const val DOMAIN = "script"
    }

    private val scriptName = entityId.takeLastWhile { it != '.' }

    override val primaryAction: Action?
        get() = Action("script", scriptName, entityId)
}