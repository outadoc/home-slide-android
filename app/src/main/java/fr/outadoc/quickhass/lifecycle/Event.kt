package fr.outadoc.quickhass.lifecycle

data class Event<T>(private val event: T) {

    private var isPopped = false

    fun peek(): T = event

    fun pop(): T {
        isPopped = true
        return event
    }
}