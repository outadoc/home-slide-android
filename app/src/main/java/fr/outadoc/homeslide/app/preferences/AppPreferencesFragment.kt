package fr.outadoc.homeslide.app.preferences

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.github.ajalt.timberkt.Timber
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.common.extensions.setupToolbar
import fr.outadoc.homeslide.shared.preferences.PreferenceRepository
import org.koin.android.ext.android.inject

class AppPreferencesFragment : PreferenceFragmentCompat() {

    private val preferenceRepository: PreferenceRepository by inject()

    private var biometricManager: BiometricManager? = null
    private var biometricPrompt: BiometricPrompt? = null

    private var preferenceHolder: PreferenceHolder? = null

    private val licenses = mapOf(
        "mit" to R.array.pref_oss_mit_summary,
        "apache2" to R.array.pref_oss_apache2_summary,
        "isc" to R.array.pref_oss_isc_summary,
        "ofl" to R.array.pref_oss_ofl_summary,
        "ccby" to R.array.pref_oss_ccby_summary
    )

    private val authCallback = object : BiometricPrompt.AuthenticationCallback() {

        private fun refreshPref() {
            preferenceHolder?.showWhenLockedPref?.isChecked = preferenceRepository.showWhenLocked
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            preferenceRepository.showWhenLocked = true
            refreshPref()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            preferenceRepository.showWhenLocked = false
            refreshPref()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            preferenceRepository.showWhenLocked = false
            refreshPref()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor, authCallback)
        biometricManager = BiometricManager.from(requireContext())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey)
        setupToolbar(R.string.title_preferences, true)

        preferenceHolder = PreferenceHolder(this).apply {
            versionPref.summary = try {
                val pInfo =
                    requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
                getString(R.string.pref_version_summary, pInfo.versionName)
            } catch (e: PackageManager.NameNotFoundException) {
                Timber.e(e)
                null
            }

            themePref.setOnPreferenceChangeListener { _, newValue ->
                (activity as? fr.outadoc.homeslide.shared.DayNightActivity)?.refreshTheme(newValue as String)
                true
            }

            showWhenLockedPref.setOnPreferenceChangeListener { _, isEnabled ->
                if (isEnabled as Boolean && biometricManager?.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
                    checkUserCredentials()

                    // Return false as long as we haven't gotten the authorization
                    false
                } else {
                    true
                }
            }
        }

        licenses.forEach { (license, content) ->
            findPreference<Preference>("pref_oss_$license")?.summary =
                resources
                    .getStringArray(content)
                    .joinToString(separator = "\n")
        }
    }

    private fun checkUserCredentials() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setDeviceCredentialAllowed(true)
            .setConfirmationRequired(false)
            .setTitle(getString(R.string.pref_authDialog_title))
            .setSubtitle(getString(R.string.pref_authDialog_subtitle))
            .build()

        biometricPrompt?.authenticate(promptInfo)
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

        biometricPrompt = null
        biometricManager = null
        preferenceHolder = null
    }

    private class PreferenceHolder(fragment: PreferenceFragmentCompat) {
        val showWhenLockedPref: SwitchPreference =
            fragment.findPreference("chk_pref_show_when_locked")!!
        val themePref: Preference = fragment.findPreference("list_pref_theme")!!
        val versionPref: Preference = fragment.findPreference("pref_about_version")!!
    }

    companion object {
        fun newInstance() = AppPreferencesFragment()
    }
}