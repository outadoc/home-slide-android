package fr.outadoc.quickhass.model

import fr.outadoc.quickhass.model.annotation.StringDomain
import fr.outadoc.quickhass.model.annotation.StringEntityId
import fr.outadoc.quickhass.model.annotation.StringState

class Entity(state: State) {

    @StringEntityId
    val entityId: String = state.entityId

    @StringState
    val stateStr: String = state.state

    @StringDomain
    val domain: String = entityId.takeWhile { it != '.' }

    val attributes: AttributeSet = state.attributes

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Entity

        if (entityId != other.entityId) return false

        return true
    }

    override fun hashCode(): Int {
        return entityId.hashCode()
    }
}