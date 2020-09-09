package fr.outadoc.homeslide.app.onboarding

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import fr.outadoc.homeslide.common.feature.daynight.DayNightActivity
import fr.outadoc.homeslide.common.feature.daynight.DayNightActivityDelegate
import fr.outadoc.homeslide.common.feature.daynight.ThemePreferenceRepository
import org.koin.android.ext.android.inject

class OnboardingActivity : AppCompatActivity(), DayNightActivity {

    private val themePrefs: ThemePreferenceRepository by inject()

    private val dayNightActivityDelegate = DayNightActivityDelegate(this, themePrefs)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        dayNightActivityDelegate.onCreate()
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet) =
        super.onCreateView(name, context, attrs)?.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(this, R.id.nav_host_fragment)
            .navigateUp()
    }

    override fun refreshTheme(updatedValue: String?) {
        dayNightActivityDelegate.refreshTheme(updatedValue)
    }
}
