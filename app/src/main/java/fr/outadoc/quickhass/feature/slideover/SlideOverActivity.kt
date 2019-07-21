package fr.outadoc.quickhass.feature.slideover

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import fr.outadoc.quickhass.DayNightActivity
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.slideover.ui.SlideOverFragment


class SlideOverActivity : DayNightActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slideover)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.slideover_content, SlideOverFragment.newInstance())
            .commit()

        window.setGravity(Gravity.BOTTOM)
        applyCompatFlags()
    }

    @Suppress("DEPRECATION")
    fun applyCompatFlags() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
    }
}
