package fr.outadoc.quickhass.feature.onboarding.model

sealed class CallStatus<out T> {
    class Done<T>(val value: Result<T>) : CallStatus<T>()
    object Loading : CallStatus<Nothing>()
}