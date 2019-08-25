package fr.outadoc.quickhass

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import fr.outadoc.quickhass.preferences.PreferenceRepository
import org.koin.android.ext.android.inject

abstract class DayNightActivity : AppCompatActivity() {

    private val prefs: PreferenceRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyPreferredTheme()
    }

    private fun applyPreferredTheme() {
        AppCompatDelegate.setDefaultNightMode(
            when (prefs.theme) {
                "day" -> AppCompatDelegate.MODE_NIGHT_NO
                "night" -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

}