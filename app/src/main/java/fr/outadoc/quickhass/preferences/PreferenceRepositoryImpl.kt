package fr.outadoc.quickhass.preferences

import android.content.Context
import android.preference.PreferenceManager


class PreferenceRepositoryImpl(context: Context) : PreferenceRepository {

    var authPrefs = PreferenceManager.getDefaultSharedPreferences(context)!!

    override val instanceBaseUrl: String
        get() = authPrefs.getString(KEY_INSTANCE_BASE_URL, "")!!

    override val altInstanceBaseUrl: String?
        get() = authPrefs.getString(KEY_INSTANCE_ALT_BASE_URL, null)

    override val accessToken: String
        get() = authPrefs.getString(KEY_ACCESS_TOKEN, "")!!

    override val theme: String
        get() = authPrefs.getString(KEY_THEME, "system")!!

    override val shouldAskForInitialValues: Boolean
        get() = accessToken == ""

    companion object {
        const val KEY_INSTANCE_BASE_URL = "et_pref_instance_base_url"
        const val KEY_INSTANCE_ALT_BASE_URL = "et_pref_instance_alt_base_url"
        const val KEY_ACCESS_TOKEN = "et_pref_auth_token"
        const val KEY_THEME = "list_pref_theme"
    }
}