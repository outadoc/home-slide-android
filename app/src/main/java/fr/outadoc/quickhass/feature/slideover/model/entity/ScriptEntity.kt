package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.feature.slideover.model.Action
import fr.outadoc.quickhass.feature.slideover.model.EntityState

class ScriptEntity(state: EntityState) : Entity(state, "file-document".toIcon()!!) {

    companion object {
        const val DOMAIN = "script"
    }

    private val scriptName = entityId.takeLastWhile { it != '.' }

    override val primaryAction: Action?
        get() = Action("script", scriptName, entityId)
}