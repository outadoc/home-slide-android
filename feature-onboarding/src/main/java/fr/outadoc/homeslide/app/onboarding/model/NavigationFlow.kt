package fr.outadoc.homeslide.app.onboarding.model

import android.net.Uri

sealed class NavigationFlow {
    object Next : NavigationFlow()
    object Back : NavigationFlow()
    class Url(val url: Uri) : NavigationFlow()
}