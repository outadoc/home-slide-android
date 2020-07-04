package fr.outadoc.homeslide.app.onboarding.feature.host.extensions

import fr.outadoc.homeslide.app.onboarding.feature.host.model.CallStatus
import fr.outadoc.homeslide.app.onboarding.feature.host.ResultIconView

fun <T> CallStatus<T>.toViewStatus(): ResultIconView.State = when (this) {
    is CallStatus.Loading -> ResultIconView.State.LOADING
    is CallStatus.Done -> when (if (value.isSuccess) value.getOrNull() else null) {
        null -> ResultIconView.State.ERROR
        else -> ResultIconView.State.SUCCESS
    }
}
