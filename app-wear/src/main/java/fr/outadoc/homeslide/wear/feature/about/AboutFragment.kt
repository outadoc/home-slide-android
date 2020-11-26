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

// PreferenceFragment is deprecated, but it's the only option on Wear OS
// as of the time of writing. ¯\_(ツ)_/¯
@file:Suppress("DEPRECATION")

package fr.outadoc.homeslide.wear.feature.about

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.crashlytics.FirebaseCrashlytics
import fr.outadoc.homeslide.common.feature.about.ThirdPartyLibraries
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.wear.R

class AboutFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences_about)

        val versionPref: Preference? = findPreference("pref_about_version")
        val enableCrashReportingPref =
            findPreference("chk_pref_enable_crash_reporting") as SwitchPreference?

        versionPref?.summary = try {
            val pInfo =
                requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            getString(R.string.pref_version_summary, pInfo.versionName)
        } catch (e: PackageManager.NameNotFoundException) {
            KLog.e(e)
            null
        }

        enableCrashReportingPref?.setOnPreferenceChangeListener { _, value ->
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(value as Boolean)
            true
        }

        ThirdPartyLibraries.licenses.forEach { (license, content) ->
            findPreference("pref_oss_$license")?.apply {
                summary = resources
                    .getStringArray(content)
                    .joinToString(separator = "\n")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = super.onCreateView(inflater, container, savedInstanceState)?.apply {
        setBackgroundColor(Color.BLACK)
    }

    private fun requireContext() = requireNotNull(context)

    companion object {
        fun newInstance() = AboutFragment()
    }
}
