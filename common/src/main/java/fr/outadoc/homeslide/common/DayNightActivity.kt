package fr.outadoc.homeslide.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

abstract class DayNightActivity : AppCompatActivity() {

    abstract val themeProvider: ThemeProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyPreferredTheme(themeProvider.preferredTheme)
    }

    private fun applyPreferredTheme(mode: String?) {
        AppCompatDelegate.setDefaultNightMode(
            when (mode) {
                "day" -> AppCompatDelegate.MODE_NIGHT_NO
                "night" -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    fun refreshTheme(updatedValue: String?) {
        applyPreferredTheme(updatedValue)
        delegate.applyDayNight()
    }
}
