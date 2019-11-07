package fr.outadoc.quickhass.rest

import fr.outadoc.quickhass.preferences.PreferenceRepository

class LongLivedTokenProviderImpl(private val prefs: PreferenceRepository) : AccessTokenProvider {

    override fun token(): String? = prefs.accessToken

    override fun refreshToken(): String? = null
}