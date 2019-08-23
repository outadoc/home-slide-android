package fr.outadoc.quickhass.preferences

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import fr.outadoc.quickhass.R


class AppPreferencesFragment : PreferenceFragmentCompat() {

    private val licenses = mapOf(
        "mit" to R.array.pref_oss_mit_summary,
        "apache2" to R.array.pref_oss_apache2_summary,
        "isc" to R.array.pref_oss_isc_summary,
        "ofl" to R.array.pref_oss_ofl_summary
    )

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey)

        findPreference<Preference>("pref_about_version")?.apply {
            summary = try {
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                getString(R.string.version_name, pInfo.versionName)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        licenses.forEach { (license, content) ->
            findPreference<Preference>("pref_oss_$license")?.summary =
                resources
                    .getStringArray(content)
                    .joinToString(separator = "\n")
        }
    }

    companion object {
        fun newInstance() = AppPreferencesFragment()
    }
}