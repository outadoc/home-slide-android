package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.feature.slideover.model.Action
import fr.outadoc.quickhass.feature.slideover.model.State

class ScriptEntity(state: State) : Entity(state, "file-document".toIcon()!!) {

    private val scriptName = entityId.takeLastWhile { it != '.' }

    override val primaryAction: Action?
        get() = Action("script", scriptName, entityId)
}