package fr.outadoc.homeslide.app.onboarding.ui

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.plusAssign
import fr.outadoc.homeslide.app.onboarding.navigation.CustomTabsNavigator

@Suppress("unused")
class OnboardingNavHostFragment : NavHostFragment() {

    override fun onCreateNavController(navController: NavController) {
        super.onCreateNavController(navController)

        context?.let {
            navController.navigatorProvider += CustomTabsNavigator(it)
        }
    }
}