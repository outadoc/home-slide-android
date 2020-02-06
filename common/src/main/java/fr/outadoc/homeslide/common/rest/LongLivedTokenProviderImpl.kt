package fr.outadoc.homeslide.common.rest

import fr.outadoc.homeslide.common.preferences.PreferenceRepository
import fr.outadoc.homeslide.rest.AccessTokenProvider

class LongLivedTokenProviderImpl(private val prefs: PreferenceRepository) :
    AccessTokenProvider {

    override fun token(): String? = prefs.accessToken

    override fun refreshToken(): String? = null
}