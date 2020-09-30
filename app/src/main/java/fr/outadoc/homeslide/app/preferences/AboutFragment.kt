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
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.common.extensions.setupToolbar
import fr.outadoc.homeslide.logging.KLog

class AboutFragment : PreferenceFragmentCompat() {

    private var preferenceHolder: PreferenceHolder? = null

    private val licenses = mapOf(
        "mit" to R.array.pref_oss_mit_summary,
        "apache2" to R.array.pref_oss_apache2_summary,
        "isc" to R.array.pref_oss_isc_summary,
        "ofl" to R.array.pref_oss_ofl_summary,
        "ccby" to R.array.pref_oss_ccby_summary,
        "ccbyncsa4" to R.array.pref_oss_ccbyncsa4_summary
    )

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

        licenses.forEach { (license, content) ->
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
