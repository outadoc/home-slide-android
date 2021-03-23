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

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.common.feature.consent.ConsentPreferenceRepository
import fr.outadoc.homeslide.common.feature.daynight.ThemePreferenceRepository
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.common.sync.DataSyncClient
import fr.outadoc.homeslide.common.sync.model.PreferencesPayload
import kotlinx.datetime.Instant

class PreferenceRepositoryImpl(
    private val context: Context,
    private val dataSyncClient: DataSyncClient
) : GlobalPreferenceRepository,
    UrlPreferenceRepository,
    TokenPreferenceRepository,
    ThemePreferenceRepository,
    ConsentPreferenceRepository,
    PreferencePublisher {

    private val appPrefs = PreferenceManager.getDefaultSharedPreferences(context)!!

    override var localInstanceBaseUrl: String?
        get() = appPrefs.getString(context.getString(R.string.pref_key_instanceBaseUrl), null)
        set(value) {
            appPrefs.edit {
                putString(context.getString(R.string.pref_key_instanceBaseUrl), value)
            }
        }

    override var remoteInstanceBaseUrl: String?
        get() = appPrefs.getString(context.getString(R.string.pref_key_instanceBaseUrlAlt), null)
        set(value) {
            appPrefs.edit {
                putString(context.getString(R.string.pref_key_instanceBaseUrlAlt), value)
            }
        }

    override var ignoreTlsErrors: Boolean
        get() = appPrefs.getBoolean(context.getString(R.string.pref_key_ignoreTlsErrors), false)
        set(value) {
            appPrefs.edit {
                putBoolean(context.getString(R.string.pref_key_ignoreTlsErrors), value)
            }
        }

    override var accessToken: String?
        get() = appPrefs.getString(context.getString(R.string.pref_key_authToken), null)
        set(value) {
            appPrefs.edit {
                putString(context.getString(R.string.pref_key_authToken), value)
            }
        }

    override var refreshToken: String?
        get() = appPrefs.getString(context.getString(R.string.pref_key_refreshToken), "")!!
        set(value) {
            appPrefs.edit {
                putString(context.getString(R.string.pref_key_refreshToken), value)
            }
        }

    override var tokenExpirationTime: Instant?
        get() = appPrefs.getString(context.getString(R.string.pref_key_tokenExpirationTime), null)
            ?.let { instantStr ->
                try {
                    Instant.parse(instantStr)
                } catch (e: Exception) {
                    null
                }
            }
        set(value) {
            appPrefs.edit {
                putString(
                    context.getString(R.string.pref_key_tokenExpirationTime),
                    value?.toString()
                )
            }
        }

    override val theme: String
        get() = appPrefs.getString(context.getString(R.string.pref_key_theme), "system")!!

    override val isCrashReportingEnabled: Boolean
        get() = appPrefs.getBoolean(context.getString(R.string.pref_key_crashlyticsEnabled), true)

    override var isOnboardingDone: Boolean
        get() = appPrefs.getBoolean(context.getString(R.string.pref_key_onboardingDone), false)
        set(value) {
            appPrefs.edit {
                putBoolean(context.getString(R.string.pref_key_onboardingDone), value)
            }
        }

    override var showWhenLocked: Boolean
        get() = appPrefs.getBoolean(context.getString(R.string.pref_key_showWhenLocked), false)
        set(value) {
            appPrefs.edit {
                putBoolean(context.getString(R.string.pref_key_showWhenLocked), value)
            }
        }

    override val refreshIntervalSeconds: Long
        get() = appPrefs.getInt(context.getString(R.string.pref_key_refreshIntervalSeconds), 10)
            .toLong()

    override fun publish() {
        val payload = PreferencesPayload(
            localInstanceBaseUrl = localInstanceBaseUrl,
            remoteInstanceBaseUrl = remoteInstanceBaseUrl,
            accessToken = accessToken,
            refreshToken = refreshToken,
            ignoreTlsErrors = ignoreTlsErrors
        )

        dataSyncClient.syncPreferences(payload)
    }
}
