package fr.outadoc.quickhass.model

import fr.outadoc.quickhass.model.annotation.StringDomain
import fr.outadoc.quickhass.model.annotation.StringEntityId
import fr.outadoc.quickhass.model.annotation.StringState

class Entity(private var state: State) {

    @StringEntityId
    val entityId: String
        get() = state.entityId

    @StringState
    val stateStr: String
        get() = state.state

    val attributes: AttributeSet
        get() = state.attributes

    @StringDomain
    val domain: String
        get() = entityId.takeWhile { it != '.' }

    fun updateState(state: State) {
        this.state = state
    }
}