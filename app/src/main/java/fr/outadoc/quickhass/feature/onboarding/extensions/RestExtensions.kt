package fr.outadoc.quickhass.feature.onboarding.extensions

import fr.outadoc.quickhass.feature.onboarding.model.CallStatus
import fr.outadoc.quickhass.feature.onboarding.ui.ResultIconView

fun <T> CallStatus<T>.toViewStatus(): ResultIconView.State = when (this) {
    is CallStatus.Loading -> ResultIconView.State.LOADING
    is CallStatus.Done -> when (if (value.isSuccess) value.getOrNull() else null) {
        null -> ResultIconView.State.ERROR
        else -> ResultIconView.State.SUCCESS
    }
}