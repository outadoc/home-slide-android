package fr.outadoc.homeslide.app.preferences

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkBuilder
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.app.onboarding.OnboardingActivity
import fr.outadoc.homeslide.common.DayNightActivity
import fr.outadoc.homeslide.common.extensions.setupToolbar
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import fr.outadoc.homeslide.hassapi.repository.AuthRepository
import fr.outadoc.homeslide.logging.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AppPreferencesFragment : PreferenceFragmentCompat() {

    private val prefs: GlobalPreferenceRepository by inject()
    private val authRepository: AuthRepository by inject()

    private var biometricManager: BiometricManager? = null
    private var biometricPrompt: BiometricPrompt? = null

    private var preferenceHolder: PreferenceHolder? = null

    private val licenses = mapOf(
        "mit" to R.array.pref_oss_mit_summary,
        "apache2" to R.array.pref_oss_apache2_summary,
        "isc" to R.array.pref_oss_isc_summary,
        "ofl" to R.array.pref_oss_ofl_summary,
        "ccby" to R.array.pref_oss_ccby_summary,
        "ccbyncsa4" to R.array.pref_oss_ccbyncsa4_summary
    )

    private val authCallback = object : BiometricPrompt.AuthenticationCallback() {

        private fun refreshPref() {
            preferenceHolder?.showWhenLockedPref?.isChecked = prefs.showWhenLocked
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            prefs.showWhenLocked = true
            refreshPref()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            prefs.showWhenLocked = false
            refreshPref()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            prefs.showWhenLocked = false
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
                KLog.e(e)
                null
            }

            themePref.setOnPreferenceChangeListener { _, newValue ->
                (activity as? DayNightActivity)?.refreshTheme(newValue as String)
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

            renewAuthPref.setOnPreferenceClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    authRepository.logout()
                }

                val pendingIntent = NavDeepLinkBuilder(requireActivity())
                    .setComponentName(OnboardingActivity::class.java)
                    .setGraph(R.navigation.nav_graph_onboarding)
                    .setDestination(R.id.setupHostFragment)
                    .createPendingIntent()

                pendingIntent.send()
                activity?.finish()
                false
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
        val renewAuthPref: Preference = fragment.findPreference("pref_renew_auth")!!
    }

    companion object {
        fun newInstance() = AppPreferencesFragment()
    }
}
