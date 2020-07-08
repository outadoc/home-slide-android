package fr.outadoc.homeslide.app.onboarding.feature.success

import android.app.ActivityManager
import fr.outadoc.homeslide.app.onboarding.navigation.NavigationEvent
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import io.uniflow.androidx.flow.AndroidDataFlow
import io.uniflow.core.flow.data.UIEvent

class SuccessViewModel(
    private val prefs: GlobalPreferenceRepository,
    private val activityManager: ActivityManager
) : AndroidDataFlow() {

    object ShowConfettiEvent : UIEvent()

    private val shouldShowConfetti: Boolean
        get() = !activityManager.isLowRamDevice

    fun onOpen() = action {
        if (shouldShowConfetti) {
            sendEvent { ShowConfettiEvent }
        }
    }

    fun onContinueClicked() = action {
        prefs.isOnboardingDone = true
        sendEvent { NavigationEvent.Next }
    }
}
