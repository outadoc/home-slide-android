package fr.outadoc.homeslide.app.onboarding.feature.host.model

sealed class CallStatus<out T> {
    class Done<T>(val value: T) : CallStatus<T>()
    object Loading : CallStatus<Nothing>()
}
