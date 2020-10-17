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

package fr.outadoc.homeslide.app.preferences

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.firebase.crashlytics.FirebaseCrashlytics
import fr.outadoc.homeslide.common.R
import fr.outadoc.homeslide.common.extensions.setupToolbar
import fr.outadoc.homeslide.common.feature.about.ThirdPartyLibraries
import fr.outadoc.homeslide.logging.KLog

class AboutFragment : PreferenceFragmentCompat() {

    private var preferenceHolder: PreferenceHolder? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_about, rootKey)
        setupToolbar(R.string.title_about, true)

        preferenceHolder = PreferenceHolder(this).apply {
            versionPref.summary = try {
                val pInfo =
                    requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
                getString(R.string.pref_version_summary, pInfo.versionName)
            } catch (e: PackageManager.NameNotFoundException) {
                KLog.e(e)
                null
            }

            enableCrashReportingPref.setOnPreferenceChangeListener { _, value ->
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(value as Boolean)
                true
            }
        }

        ThirdPartyLibraries.licenses.forEach { (license, content) ->
            findPreference<Preference>("pref_oss_$license")?.apply {
                summary = resources
                    .getStringArray(content)
                    .joinToString(separator = "\n")

                if (summary.isNullOrBlank()) {
                    isVisible = false
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.setWindowInsets()
    }

    private fun View.setWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            v.setPadding(
                v.paddingLeft,
                v.paddingTop,
                v.paddingRight,
                v.paddingBottom + insets.systemWindowInsetBottom
            )

            WindowInsetsCompat.Builder()
                .setSystemWindowInsets(
                    Insets.of(
                        insets.systemWindowInsetLeft,
                        insets.systemWindowInsetTop,
                        insets.systemWindowInsetRight,
                        0
                    )
                )
                .build()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceHolder = null
    }

    private class PreferenceHolder(fragment: PreferenceFragmentCompat) {
        val versionPref: Preference = fragment.findPreference("pref_about_version")!!
        val enableCrashReportingPref: SwitchPreference =
            fragment.findPreference("chk_pref_enable_crash_reporting")!!
    }

    companion object {
        fun newInstance() = AboutFragment()
    }
}
