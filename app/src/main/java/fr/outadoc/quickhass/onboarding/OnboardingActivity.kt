package fr.outadoc.quickhass.onboarding

import android.os.Bundle
import androidx.navigation.Navigation.findNavController
import fr.outadoc.quickhass.DayNightActivity
import fr.outadoc.quickhass.R

class OnboardingActivity : DayNightActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        findNavController(
            this,
            R.id.nav_host_fragment
        ).navigate(R.id.welcomeFragment)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(this, R.id.nav_host_fragment)
                .navigateUp()
    }
}