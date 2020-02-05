package fr.outadoc.homeslide.common.rest

import fr.outadoc.homeslide.shared.preferences.PreferenceRepository

class LongLivedTokenProviderImpl(private val prefs: PreferenceRepository) : AccessTokenProvider {

    override fun token(): String? = prefs.accessToken

    override fun refreshToken(): String? = null
}