package fr.outadoc.quickhass.preferences

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.ajalt.timberkt.Timber
import fr.outadoc.quickhass.DayNightActivity
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.extensions.setupToolbar

class AppPreferencesFragment : PreferenceFragmentCompat() {

    private val licenses = mapOf(
        "mit" to R.array.pref_oss_mit_summary,
        "apache2" to R.array.pref_oss_apache2_summary,
        "isc" to R.array.pref_oss_isc_summary,
        "ofl" to R.array.pref_oss_ofl_summary,
        "ccby" to R.array.pref_oss_ccby_summary
    )

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey)
        setupToolbar(R.string.title_preferences, true)

        findPreference<Preference>("pref_about_version")?.apply {
            summary = try {
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                getString(R.string.pref_version_summary, pInfo.versionName)
            } catch (e: PackageManager.NameNotFoundException) {
                Timber.e(e)
                null
            }
        }

        findPreference<Preference>("list_pref_theme")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                (activity as? DayNightActivity)?.refreshTheme(newValue as String)
                true
            }
        }

        licenses.forEach { (license, content) ->
            findPreference<Preference>("pref_oss_$license")?.summary =
                resources
                    .getStringArray(content)
                    .joinToString(separator = "\n")
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

    companion object {
        fun newInstance() = AppPreferencesFragment()
    }
}