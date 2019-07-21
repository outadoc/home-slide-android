package fr.outadoc.quickhass.preferences

import android.os.Bundle
import fr.outadoc.quickhass.DayNightActivity

class SettingsActivity : DayNightActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, AppPreferencesFragment.newInstance())
            .commit()
    }

}
