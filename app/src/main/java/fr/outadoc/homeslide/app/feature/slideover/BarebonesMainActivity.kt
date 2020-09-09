package fr.outadoc.homeslide.app.feature.slideover

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.app.feature.grid.ui.EntityGridFragment
import fr.outadoc.homeslide.common.feature.daynight.DayNightActivity
import fr.outadoc.homeslide.common.feature.daynight.DayNightActivityDelegate
import fr.outadoc.homeslide.common.feature.daynight.ThemePreferenceRepository
import org.koin.android.ext.android.inject

class BarebonesMainActivity : AppCompatActivity(), DayNightActivity {

    private val themePrefs: ThemePreferenceRepository by inject()
    private val dayNightActivityDelegate = DayNightActivityDelegate(this, themePrefs)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barebones)

        dayNightActivityDelegate.onCreate()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.activity_main_content, EntityGridFragment.newInstance())
            .commit()
    }

    override fun refreshTheme(updatedValue: String?) {
        dayNightActivityDelegate.refreshTheme(updatedValue)
    }
}
