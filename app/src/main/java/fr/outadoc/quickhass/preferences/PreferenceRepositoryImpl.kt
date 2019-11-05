package fr.outadoc.quickhass.preferences

import android.content.Context
import androidx.preference.PreferenceManager

class PreferenceRepositoryImpl(context: Context) : PreferenceRepository {

    private val appPrefs = PreferenceManager.getDefaultSharedPreferences(context)!!

    override var instanceBaseUrl: String
        get() = appPrefs.getString(KEY_INSTANCE_BASE_URL, "")!!
        set(value) {
            appPrefs.edit().putString(KEY_INSTANCE_BASE_URL, value).apply()
        }

    override var altInstanceBaseUrl: String?
        get() = appPrefs.getString(KEY_INSTANCE_ALT_BASE_URL, null)
        set(value) {
            appPrefs.edit().putString(KEY_INSTANCE_ALT_BASE_URL, value).apply()
        }

    override var accessToken: String
        get() = appPrefs.getString(KEY_ACCESS_TOKEN, "")!!
        set(value) {
            appPrefs.edit().putString(KEY_ACCESS_TOKEN, value).apply()
        }

    override var preferredBaseUrl: PreferredBaseUrl
        get() {
            val value = appPrefs.getString(KEY_PREFERRED_BASE_URL, PreferredBaseUrl.PRIMARY.id)!!
            return PreferredBaseUrl.values().first { it.id == value }
        }
        set(value) {
            appPrefs.edit().putString(KEY_PREFERRED_BASE_URL, value.id).apply()
        }

    override val theme: String
        get() = appPrefs.getString(KEY_THEME, "system")!!

    override var isOnboardingDone: Boolean
        get() = appPrefs.getBoolean(KEY_IS_ONBOARDING_DONE, false)
        set(value) {
            appPrefs.edit().putBoolean(KEY_IS_ONBOARDING_DONE, value).apply()
        }

    override val showWhenLocked: Boolean
        get() = appPrefs.getBoolean(KEY_SHOW_WHEN_LOCKED, false)

    override val refreshIntervalSeconds: Long
        get() = appPrefs.getInt(KEY_REFRESH_INTERVAL, 10).toLong()

    companion object {
        const val KEY_INSTANCE_BASE_URL = "et_pref_instance_base_url"
        const val KEY_INSTANCE_ALT_BASE_URL = "et_pref_instance_alt_base_url"
        const val KEY_PREFERRED_BASE_URL = "enum_pref_preferred_base_url"
        const val KEY_ACCESS_TOKEN = "et_pref_auth_token"
        const val KEY_THEME = "list_pref_theme"
        const val KEY_IS_ONBOARDING_DONE = "chk_pref_onboarding_ok"
        const val KEY_SHOW_WHEN_LOCKED = "chk_pref_show_when_locked"
        const val KEY_REFRESH_INTERVAL = "seek_pref_refresh_interval_s"
    }
}