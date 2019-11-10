package fr.outadoc.quickhass.feature.slideover

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import fr.outadoc.quickhass.DayNightActivity
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.extensions.isInteractive
import fr.outadoc.quickhass.extensions.setShowWhenLockedCompat
import fr.outadoc.quickhass.feature.slideover.ui.SlideOverFragment
import fr.outadoc.quickhass.preferences.PreferenceRepository
import org.koin.android.ext.android.inject


class SlideOverActivity : DayNightActivity() {

    private val prefs: PreferenceRepository by inject()

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
