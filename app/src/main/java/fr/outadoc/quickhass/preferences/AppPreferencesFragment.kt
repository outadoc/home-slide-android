package fr.outadoc.quickhass.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import fr.outadoc.quickhass.R


class AppPreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey)
    }

    companion object {
        fun newInstance() = AppPreferencesFragment()
    }
}