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

package fr.outadoc.homeslide.app.feature.slideover

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.app.feature.slideover.ui.SlideOverFragment
import fr.outadoc.homeslide.app.preferences.PreferencePublisher
import fr.outadoc.homeslide.common.extensions.isInteractive
import fr.outadoc.homeslide.common.extensions.setShowWhenLockedCompat
import fr.outadoc.homeslide.common.feature.consent.ConsentPreferenceRepository
import fr.outadoc.homeslide.common.feature.daynight.DayNightActivity
import fr.outadoc.homeslide.common.feature.daynight.DayNightActivityDelegate
import fr.outadoc.homeslide.common.feature.daynight.ThemePreferenceRepository
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import org.koin.android.ext.android.inject

class SlideOverActivity : AppCompatActivity(), DayNightActivity {

    private val prefs: GlobalPreferenceRepository by inject()
    private val themePrefs: ThemePreferenceRepository by inject()
    private val consentPrefs: ConsentPreferenceRepository by inject()
    private val prefPublisher: PreferencePublisher by inject()

    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        prefPublisher.publish()
    }

    private val dayNightActivityDelegate = DayNightActivityDelegate(this, themePrefs)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slideover)

        dayNightActivityDelegate.onCreate()

        FirebaseCrashlytics.getInstance()
            .setCrashlyticsCollectionEnabled(consentPrefs.isCrashReportingEnabled)

        // Sync with watch, just in case this hasn't been done yet
        prefPublisher.publish()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.slideover_content, SlideOverFragment.newInstance())
            .commit()

        window.setGravity(Gravity.BOTTOM)
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet) =
        super.onCreateView(name, context, attrs)?.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

    override fun onResume() {
        super.onResume()

        PreferenceManager.getDefaultSharedPreferences(this)?.apply {
            registerOnSharedPreferenceChangeListener(prefChangeListener)
        }

        setShowWhenLockedCompat(prefs.showWhenLocked)
    }

    override fun onPause() {
        PreferenceManager.getDefaultSharedPreferences(this)?.apply {
            unregisterOnSharedPreferenceChangeListener(prefChangeListener)
        }

        if (!isInteractive()) {
            // Close slideover when locking the screen
            finish()
        }

        super.onPause()
    }

    override fun refreshTheme(updatedValue: String?) {
        dayNightActivityDelegate.refreshTheme(updatedValue)
    }
}
