package fr.outadoc.quickhass.model

class Entity(private var state: State) {

    val entityId: String
        get() = state.entityId

    val stateStr: String
        get() = state.state

    val attributes: AttributeSet
        get() = state.attributes

    val domain: String
        get() = entityId.takeWhile { it != '.' }

    fun updateState(state: State) {
        this.state = state
    }
}