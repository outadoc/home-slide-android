/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
