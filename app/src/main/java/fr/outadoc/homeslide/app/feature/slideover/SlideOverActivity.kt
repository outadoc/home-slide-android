package fr.outadoc.homeslide.app.feature.slideover

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.app.feature.slideover.ui.SlideOverFragment
import fr.outadoc.homeslide.common.DayNightActivity
import fr.outadoc.homeslide.common.ThemeProvider
import fr.outadoc.homeslide.common.extensions.isInteractive
import fr.outadoc.homeslide.common.extensions.setShowWhenLockedCompat
import fr.outadoc.homeslide.common.preferences.PreferenceRepository
import org.koin.android.ext.android.inject

class SlideOverActivity : DayNightActivity() {

    val prefs: PreferenceRepository by inject()

    override val themeProvider: ThemeProvider = object :
        ThemeProvider {
        override val preferredTheme: String?
            get() = prefs.theme
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slideover)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.slideover_content, SlideOverFragment.newInstance())
            .commit()

        window.setGravity(Gravity.BOTTOM)
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet) =
        super.onCreateView(name, context, attrs)?.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

    override fun onResume() {
        super.onResume()
        setShowWhenLockedCompat(prefs.showWhenLocked)
    }

    override fun onPause() {
        super.onPause()

        if (!isInteractive()) {
            // Close slideover when locking the screen
            finish()
        }
    }
}
