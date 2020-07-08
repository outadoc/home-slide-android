package fr.outadoc.homeslide.app.onboarding.feature.shortcuts

import fr.outadoc.homeslide.app.onboarding.navigation.NavigationEvent
import io.uniflow.androidx.flow.AndroidDataFlow

class ShortcutSetupViewModel : AndroidDataFlow() {

    fun onContinueClicked() = action {
        sendEvent { NavigationEvent.Next }
    }
}
