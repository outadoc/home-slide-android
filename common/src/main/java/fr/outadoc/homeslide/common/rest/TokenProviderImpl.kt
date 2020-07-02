package fr.outadoc.homeslide.common.rest

import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.hassapi.repository.AuthRepository
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.rest.auth.AccessTokenProvider
import java.time.Instant
import kotlinx.coroutines.runBlocking

class TokenProviderImpl(
    private val prefs: TokenPreferenceRepository,
    private val authRepository: AuthRepository
) : AccessTokenProvider {

    override val isTokenExpired: Boolean
        get() = prefs.accessToken == null ||
            prefs.tokenExpirationTime == null ||
            Instant.now().isAfter(prefs.tokenExpirationTime)

    override fun getOrRefreshToken(): String? {
        return when {
            prefs.accessToken == null -> null
            isTokenExpired -> doRefreshToken()
            else -> prefs.accessToken
        }
    }

    override fun doRefreshToken(): String? {
        runBlocking { authRepository.refreshToken() }
            .onFailure { e ->
                KLog.e(e)
                return null
            }
            .onSuccess { token ->
                prefs.accessToken = token.accessToken
                prefs.tokenExpirationTime = Instant.now().plusSeconds(token.expiresIn)
                return token.accessToken
            }

        return null
    }
}
