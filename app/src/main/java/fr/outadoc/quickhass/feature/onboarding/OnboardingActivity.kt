package fr.outadoc.quickhass.feature.onboarding

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.navigation.Navigation.findNavController
import fr.outadoc.quickhass.DayNightActivity
import fr.outadoc.quickhass.R

class OnboardingActivity : DayNightActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        setSupportActionBar(findViewById(R.id.toolbar))

        if (savedInstanceState == null) {
            findNavController(this, R.id.nav_host_fragment)
                .navigate(R.id.welcomeFragment)
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet) =
        super.onCreateView(name, context, attrs)?.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(this, R.id.nav_host_fragment)
            .navigateUp()
    }
}