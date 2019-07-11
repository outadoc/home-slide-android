package fr.outadoc.quickhass.onboarding

import android.os.Bundle
import fr.outadoc.quickhass.DayNightActivity
import fr.outadoc.quickhass.R

class OnboardingActivity : DayNightActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
    }
}