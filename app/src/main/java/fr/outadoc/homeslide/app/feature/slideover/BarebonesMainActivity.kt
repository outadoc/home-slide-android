package fr.outadoc.homeslide.app.feature.slideover

import android.os.Bundle
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.app.feature.grid.ui.EntityGridFragment
import fr.outadoc.homeslide.common.DayNightActivity
import fr.outadoc.homeslide.common.ThemeProvider
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import org.koin.android.ext.android.inject

class BarebonesMainActivity : DayNightActivity() {

    private val prefs: GlobalPreferenceRepository by inject()

    override val themeProvider: ThemeProvider = object :
        ThemeProvider {
        override val preferredTheme: String?
            get() = prefs.theme
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barebones)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.activity_main_content, EntityGridFragment.newInstance())
            .commit()
    }
}
