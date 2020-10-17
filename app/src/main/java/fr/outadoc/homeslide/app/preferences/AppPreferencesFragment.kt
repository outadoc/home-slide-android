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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.app.onboarding.OnboardingActivity
import fr.outadoc.homeslide.common.extensions.setupToolbar
import fr.outadoc.homeslide.common.feature.daynight.DayNightActivity
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import fr.outadoc.homeslide.hassapi.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AppPreferencesFragment : PreferenceFragmentCompat() {

    private val prefs: GlobalPreferenceRepository by inject()
    private val authRepository: AuthRepository by inject()

    private var biometricManager: BiometricManager? = null
    private var biometricPrompt: BiometricPrompt? = null

    private var preferenceHolder: PreferenceHolder? = null

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
                val pendingIntent = NavDeepLinkBuilder(requireActivity())
                    .setComponentName(OnboardingActivity::class.java)
                    .setGraph(R.navigation.nav_graph_onboarding)
                    .setDestination(R.id.setupHostFragment)
                    .createPendingIntent()

                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.pref_logoutDialog_message)
                    .setPositiveButton(R.string.pref_logoutDialog_positive) { _, _ ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            authRepository.logout()
                        }

                        pendingIntent.send()
                        activity?.finish()
                    }
                    .setNegativeButton(R.string.pref_logoutDialog_negative) { _, _ -> }
                    .show()

                false
            }

            shortcutHelpPref.setOnPreferenceClickListener {
                val pendingIntent = NavDeepLinkBuilder(requireActivity())
                    .setComponentName(OnboardingActivity::class.java)
                    .setGraph(R.navigation.nav_graph_onboarding)
                    .setDestination(R.id.setupShortcutFragment)
                    .createPendingIntent()

                pendingIntent.send()
                activity?.finish()

                false
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
        val renewAuthPref: Preference = fragment.findPreference("pref_renew_auth")!!
        val shortcutHelpPref: Preference = fragment.findPreference("pref_help_shortcuts")!!
    }

    companion object {
        fun newInstance() = AppPreferencesFragment()
    }
}
