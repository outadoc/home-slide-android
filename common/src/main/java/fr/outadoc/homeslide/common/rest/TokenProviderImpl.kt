package fr.outadoc.homeslide.common.rest

import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.hassapi.model.auth.expiresInDuration
import fr.outadoc.homeslide.hassapi.repository.AuthRepository
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.rest.auth.AccessTokenProvider
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock

class TokenProviderImpl(
    private val prefs: TokenPreferenceRepository,
    private val authRepository: AuthRepository,
    private val clock: Clock
) : AccessTokenProvider {

    override val isTokenExpired: Boolean
        get() = prefs.accessToken == null ||
            prefs.tokenExpirationTime?.let { expiration ->
            clock.now() > expiration
        } ?: true

    override fun getOrRefreshToken(): String? {
        return when {
            prefs.accessToken == null -> null
            isTokenExpired -> doRefreshToken()
            else -> prefs.accessToken
        }
    }

    override fun doRefreshToken(): String? {
        return try {
            val token = runBlocking { authRepository.refreshToken() }
            prefs.accessToken = token.accessToken
            prefs.tokenExpirationTime = clock.now() + token.expiresInDuration
            token.accessToken
        } catch (e: Exception) {
            KLog.e(e)
            null
        }
    }
}
