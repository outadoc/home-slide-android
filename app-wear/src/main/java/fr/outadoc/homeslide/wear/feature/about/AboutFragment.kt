// PreferenceFragment is deprecated, but it's the only option on Wear OS
// as of the time of writing. ¯\_(ツ)_/¯
@file:Suppress("DEPRECATION")

package fr.outadoc.homeslide.wear.feature.about

import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import com.google.firebase.crashlytics.FirebaseCrashlytics
import fr.outadoc.homeslide.common.feature.about.ThirdPartyLibraries
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.wear.R

class AboutFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences_about)

        val versionPref: Preference? = findPreference("pref_about_version")
        val enableCrashReportingPref = findPreference("chk_pref_enable_crash_reporting") as SwitchPreference?

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

    private fun requireContext() = requireNotNull(context)

    companion object {
        fun newInstance() = AboutFragment()
    }
}
