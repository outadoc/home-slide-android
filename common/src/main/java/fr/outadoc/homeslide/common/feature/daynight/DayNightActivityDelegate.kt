package fr.outadoc.homeslide.common.feature.daynight

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class DayNightActivityDelegate(
    private val activity: AppCompatActivity,
    private val prefs: ThemePreferenceRepository
) {
    fun onCreate() {
        applyPreferredTheme(prefs.theme)
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
        activity.delegate.applyDayNight()
    }
}
