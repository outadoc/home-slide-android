package fr.outadoc.quickhass.preferences

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import fr.outadoc.quickhass.R


class AppPreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey)

        findPreference<Preference>("pref_about_version")?.apply {
            try {
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                summary = getString(R.string.version_name, pInfo.versionName)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                summary = null
            }
        }
    }

    companion object {
        fun newInstance() = AppPreferencesFragment()
    }
}