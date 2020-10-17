/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
            builder.setStartAnimations(context, enterAnim, exitAnim)
            builder.setExitAnimations(context, popEnterAnim, popExitAnim)
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
