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

package fr.outadoc.homeslide.wear.preferences

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import fr.outadoc.homeslide.common.feature.consent.ConsentPreferenceRepository
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.wear.R
import kotlinx.datetime.Instant

class WearPreferenceRepositoryImpl(private val context: Context) :
    GlobalPreferenceRepository,
    UrlPreferenceRepository,
    TokenPreferenceRepository,
    ConsentPreferenceRepository {

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

    override val refreshIntervalSeconds: Long
        get() = 10

    override var showWhenLocked: Boolean
        get() = false
        set(_) {}

    override val isCrashReportingEnabled: Boolean
        get() = appPrefs.getBoolean(context.getString(R.string.pref_key_crashlyticsEnabled), true)

    override var isOnboardingDone: Boolean
        get() = appPrefs.getBoolean(context.getString(R.string.pref_key_onboardingDone), false)
        set(value) {
            appPrefs.edit {
                putBoolean(context.getString(R.string.pref_key_onboardingDone), value)
            }
        }
}
