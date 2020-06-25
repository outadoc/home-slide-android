package fr.outadoc.homeslide.common.rest

import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.hassapi.repository.AuthRepository
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.rest.auth.AccessTokenProvider
import kotlinx.coroutines.runBlocking

class TokenProviderImpl(
    private val prefs: TokenPreferenceRepository,
    private val authRepository: AuthRepository
) : AccessTokenProvider {

    override fun getToken(): String? = prefs.accessToken

    override fun refreshToken(): String? {
        runBlocking { authRepository.refreshToken() }
            .onFailure { e ->
                KLog.e(e)
                return null
            }
            .onSuccess { token ->
                prefs.accessToken = token.accessToken
                return token.accessToken
            }

        return null
    }
}
