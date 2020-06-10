package fr.outadoc.quickhass.wear.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.rest.baseurl.PreferredBaseUrl

class WearPreferenceRepositoryImpl(context: Context) : GlobalPreferenceRepository,
    UrlPreferenceRepository, TokenPreferenceRepository {

    private val appPrefs = PreferenceManager.getDefaultSharedPreferences(context)!!

    override var instanceBaseUrl: String?
        get() = appPrefs.getString(KEY_INSTANCE_BASE_URL, null)
        set(value) {
            appPrefs.edit().putString(KEY_INSTANCE_BASE_URL, value).apply()
        }

    override var altInstanceBaseUrl: String?
        get() = appPrefs.getString(KEY_INSTANCE_ALT_BASE_URL, null)
        set(value) {
            appPrefs.edit().putString(KEY_INSTANCE_ALT_BASE_URL, value).apply()
        }

    override var accessToken: String?
        get() = appPrefs.getString(KEY_ACCESS_TOKEN, null)
        set(value) {
            appPrefs.edit().putString(KEY_ACCESS_TOKEN, value).apply()
        }

    override var refreshToken: String?
        get() = appPrefs.getString(KEY_REFRESH_TOKEN, "")!!
        set(value) {
            appPrefs.edit().putString(KEY_REFRESH_TOKEN, value).apply()
        }

    override var preferredBaseUrl: PreferredBaseUrl
        get() {
            val value = appPrefs.getString(KEY_PREFERRED_BASE_URL, PreferredBaseUrl.PRIMARY.id)!!
            return PreferredBaseUrl.values().first { it.id == value }
        }
        set(value) {
            appPrefs.edit().putString(KEY_PREFERRED_BASE_URL, value.id).apply()
        }

    override val refreshIntervalSeconds: Long
        get() = 10

    override var showWhenLocked: Boolean
        get() = false
        set(_) {}

    override val theme: String
        get() = "day"

    override var isOnboardingDone: Boolean
        get() = true
        set(_) {}

    companion object {
        const val KEY_INSTANCE_BASE_URL = "et_pref_instance_base_url"
        const val KEY_INSTANCE_ALT_BASE_URL = "et_pref_instance_alt_base_url"
        const val KEY_PREFERRED_BASE_URL = "enum_pref_preferred_base_url"
        const val KEY_ACCESS_TOKEN = "et_pref_auth_token"
        const val KEY_REFRESH_TOKEN = "et_pref_refresh_token"
    }
}