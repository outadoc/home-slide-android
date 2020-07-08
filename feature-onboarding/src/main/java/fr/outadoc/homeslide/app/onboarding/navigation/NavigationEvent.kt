package fr.outadoc.homeslide.app.onboarding.navigation

import android.net.Uri
import io.uniflow.core.flow.data.UIEvent

sealed class NavigationEvent : UIEvent() {
    object Next : NavigationEvent()
    object Back : NavigationEvent()
    class Url(val url: Uri) : NavigationEvent()
}
