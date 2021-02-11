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
import kotlinx.datetime.Instant

class WearPreferenceRepositoryImpl(context: Context) :
    GlobalPreferenceRepository,
    UrlPreferenceRepository,
    TokenPreferenceRepository,
    ConsentPreferenceRepository {

    private val appPrefs = PreferenceManager.getDefaultSharedPreferences(context)!!

    override var localInstanceBaseUrl: String?
        get() = appPrefs.getString(KEY_INSTANCE_BASE_URL, null)
        set(value) {
            appPrefs.edit {
                putString(KEY_INSTANCE_BASE_URL, value)
            }
        }

    override var remoteInstanceBaseUrl: String?
        get() = appPrefs.getString(KEY_INSTANCE_ALT_BASE_URL, null)
        set(value) {
            appPrefs.edit {
                putString(KEY_INSTANCE_ALT_BASE_URL, value)
            }
        }

    override var accessToken: String?
        get() = appPrefs.getString(KEY_ACCESS_TOKEN, null)
        set(value) {
            appPrefs.edit {
                putString(KEY_ACCESS_TOKEN, value)
            }
        }

    override var refreshToken: String?
        get() = appPrefs.getString(KEY_REFRESH_TOKEN, "")!!
        set(value) {
            appPrefs.edit {
                putString(KEY_REFRESH_TOKEN, value)
            }
        }

    override var tokenExpirationTime: Instant?
        get() = appPrefs.getString(KEY_TOKEN_EXPIRATION_TIME, null)?.let { instantStr ->
            try {
                Instant.parse(instantStr)
            } catch (e: Exception) {
                null
            }
        }
        set(value) {
            appPrefs.edit {
                putString(KEY_TOKEN_EXPIRATION_TIME, value?.toString())
            }
        }

    override val refreshIntervalSeconds: Long
        get() = 10

    override var showWhenLocked: Boolean
        get() = false
        set(_) {}

    override val isCrashReportingEnabled: Boolean
        get() = appPrefs.getBoolean(KEY_ENABLE_CRASH_REPORTING, true)

    override var isOnboardingDone: Boolean
        get() = appPrefs.getBoolean(KEY_IS_ONBOARDING_DONE, false)
        set(value) {
            appPrefs.edit {
                putBoolean(KEY_IS_ONBOARDING_DONE, value)
            }
        }

    companion object {
        const val KEY_INSTANCE_BASE_URL = "et_pref_instance_base_url"
        const val KEY_INSTANCE_ALT_BASE_URL = "et_pref_instance_alt_base_url"
        const val KEY_ACCESS_TOKEN = "et_pref_auth_token"
        const val KEY_REFRESH_TOKEN = "et_pref_refresh_token"
        const val KEY_TOKEN_EXPIRATION_TIME = "et_pref_token_expiration_time"
        const val KEY_IS_ONBOARDING_DONE = "chk_pref_onboarding_ok"
        const val KEY_ENABLE_CRASH_REPORTING = "chk_pref_enable_crash_reporting"
    }
}
