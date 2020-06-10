package fr.outadoc.homeslide.app.onboarding

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.navigation.Navigation.findNavController
import fr.outadoc.homeslide.common.DayNightActivity
import fr.outadoc.homeslide.common.ThemeProvider
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import org.koin.android.ext.android.inject

class OnboardingActivity : DayNightActivity() {

    val prefs: GlobalPreferenceRepository by inject()

    override val themeProvider: ThemeProvider = object :
        ThemeProvider {
        override val preferredTheme: String?
            get() = prefs.theme
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
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