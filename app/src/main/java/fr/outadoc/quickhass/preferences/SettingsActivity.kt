package fr.outadoc.quickhass.preferences

import android.os.Bundle
import fr.outadoc.quickhass.DayNightActivity
import fr.outadoc.quickhass.R

class SettingsActivity : DayNightActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, AppPreferencesFragment.newInstance())
            .commit()
    }

}
