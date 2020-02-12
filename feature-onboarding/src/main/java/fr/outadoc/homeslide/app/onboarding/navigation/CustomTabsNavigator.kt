package fr.outadoc.homeslide.app.onboarding.navigation

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

@Navigator.Name("custom-tabs")
class CustomTabsNavigator(
    private val context: Context
) : Navigator<CustomTabsNavigator.Destination>() {

    override fun createDestination() = Destination(this)

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ): NavDestination? {
        val builder = CustomTabsIntent.Builder()

        navOptions?.apply {
            builder.setStartAnimations(context, enterAnim, popEnterAnim)
            builder.setExitAnimations(context, exitAnim, popExitAnim)
        }

        val intent = builder.build()
        val uri = args?.getParcelable<Uri>("uri")
            ?: throw IllegalArgumentException("uri must be passed as an argument")

        intent.launchUrl(context, uri)
        return null
    }

    override fun popBackStack() = true

    @NavDestination.ClassType(Activity::class)
    class Destination(navigator: Navigator<out NavDestination>) : NavDestination(navigator)
}