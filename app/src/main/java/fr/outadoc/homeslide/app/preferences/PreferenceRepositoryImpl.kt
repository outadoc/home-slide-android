package fr.outadoc.homeslide.app.preferences

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import fr.outadoc.homeslide.common.feature.consent.ConsentPreferenceRepository
import fr.outadoc.homeslide.common.feature.daynight.ThemePreferenceRepository
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.common.sync.DataSyncClient
import fr.outadoc.homeslide.common.sync.model.PreferencesPayload
import fr.outadoc.homeslide.rest.baseurl.PreferredBaseUrl
import kotlinx.datetime.Instant

class PreferenceRepositoryImpl(
    context: Context,
    private val dataSyncClient: DataSyncClient
) : GlobalPreferenceRepository,
    UrlPreferenceRepository,
    TokenPreferenceRepository,
    ThemePreferenceRepository,
    ConsentPreferenceRepository,
    PreferencePublisher {

    private val appPrefs = PreferenceManager.getDefaultSharedPreferences(context)!!

    override var instanceBaseUrl: String?
        get() = appPrefs.getString(KEY_INSTANCE_BASE_URL, null)
        set(value) {
            appPrefs.edit {
                putString(KEY_INSTANCE_BASE_URL, value)
            }
        }

    override var altInstanceBaseUrl: String?
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

    override var preferredBaseUrl: PreferredBaseUrl
        get() {
            val value = appPrefs.getString(KEY_PREFERRED_BASE_URL, PreferredBaseUrl.PRIMARY.id)!!
            return PreferredBaseUrl.values().first { it.id == value }
        }
        set(value) {
            appPrefs.edit {
                putString(KEY_PREFERRED_BASE_URL, value.id)
            }
        }

    override val theme: String
        get() = appPrefs.getString(KEY_THEME, "system")!!

    override val isCrashReportingEnabled: Boolean
        get() = appPrefs.getBoolean(KEY_ENABLE_CRASH_REPORTING, true)

    override var isOnboardingDone: Boolean
        get() = appPrefs.getBoolean(KEY_IS_ONBOARDING_DONE, false)
        set(value) {
            appPrefs.edit {
                putBoolean(KEY_IS_ONBOARDING_DONE, value)
            }
        }

    override var showWhenLocked: Boolean
        get() = appPrefs.getBoolean(KEY_SHOW_WHEN_LOCKED, false)
        set(value) {
            appPrefs.edit {
                putBoolean(KEY_SHOW_WHEN_LOCKED, value)
            }
        }

    override val refreshIntervalSeconds: Long
        get() = appPrefs.getInt(KEY_REFRESH_INTERVAL, 10).toLong()

    override fun publish() {
        val payload = PreferencesPayload(
            instanceBaseUrl = instanceBaseUrl,
            altInstanceBaseUrl = altInstanceBaseUrl,
            accessToken = accessToken,
            refreshToken = refreshToken
        )

        dataSyncClient.syncPreferences(payload)
    }

    companion object {
        const val KEY_INSTANCE_BASE_URL = "et_pref_instance_base_url"
        const val KEY_INSTANCE_ALT_BASE_URL = "et_pref_instance_alt_base_url"
        const val KEY_PREFERRED_BASE_URL = "enum_pref_preferred_base_url"
        const val KEY_ACCESS_TOKEN = "et_pref_auth_token"
        const val KEY_REFRESH_TOKEN = "et_pref_refresh_token"
        const val KEY_TOKEN_EXPIRATION_TIME = "et_pref_token_expiration_time"
        const val KEY_THEME = "list_pref_theme"
        const val KEY_IS_ONBOARDING_DONE = "chk_pref_onboarding_ok"
        const val KEY_SHOW_WHEN_LOCKED = "chk_pref_show_when_locked"
        const val KEY_REFRESH_INTERVAL = "seek_pref_refresh_interval_s"
        const val KEY_ENABLE_CRASH_REPORTING = "chk_pref_enable_crash_reporting"
    }
}
