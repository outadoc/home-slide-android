/*
 * Copyright 2021 Baptiste Candellier
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

package fr.outadoc.homeslide.common.tls

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import fr.outadoc.homeslide.common.R
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.rest.tls.TlsConfigurationChangedListener
import fr.outadoc.homeslide.rest.tls.TlsConfigurationProvider

class PreferenceTlsConfigurationProvider(
    context: Context,
    private val urlPreferenceRepository: UrlPreferenceRepository
) : TlsConfigurationProvider {

    private val changeListeners = mutableListOf<TlsConfigurationChangedListener>()

    private val isTlsCheckEnabledKey = context.getString(R.string.pref_key_ignoreTlsErrors)
    private val prefsChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == isTlsCheckEnabledKey) {
            changeListeners.forEach { it.onTlsConfigurationChanged() }
        }
    }

    init {
        PreferenceManager.getDefaultSharedPreferences(context)
            .registerOnSharedPreferenceChangeListener(prefsChangeListener)
    }

    override val isCertificateCheckEnabled
        get() = !urlPreferenceRepository.ignoreTlsErrors

    override fun addCertificateCheckEnabledChangedListener(listener: TlsConfigurationChangedListener) {
        changeListeners.add(listener)
    }
}
