package fr.outadoc.homeslide.util.lifecycle

data class Event<T>(private val event: T) {

    private var isPopped = false

    fun peek(): T = event

    fun pop(): T? {
        return if (isPopped) null else {
            isPopped = true
            event
        }
    }
}