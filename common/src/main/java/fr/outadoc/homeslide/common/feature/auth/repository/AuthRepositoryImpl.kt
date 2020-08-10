package fr.outadoc.homeslide.common.feature.auth.repository

import com.squareup.moshi.Moshi
import fr.outadoc.homeslide.common.feature.auth.AuthError
import fr.outadoc.homeslide.common.feature.auth.ErrorCodes
import fr.outadoc.homeslide.common.feature.auth.InvalidRefreshTokenException
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.hassapi.api.AuthApi
import fr.outadoc.homeslide.hassapi.model.auth.Token
import fr.outadoc.homeslide.hassapi.repository.AuthRepository
import fr.outadoc.homeslide.rest.auth.OAuthConfiguration
import fr.outadoc.homeslide.rest.util.getResponseOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val tokenPrefs: TokenPreferenceRepository,
    private val prefs: GlobalPreferenceRepository,
    private val oAuthConfiguration: OAuthConfiguration,
    private val api: AuthApi,
    moshi: Moshi
) : AuthRepository {

    private val errorAdapter = moshi.adapter(AuthError::class.java)

    override suspend fun getToken(code: String): Token {
        return api.getToken(code, oAuthConfiguration.clientId).getResponseOrThrow()
    }

    override suspend fun refreshToken(): Token {
        val refreshToken = tokenPrefs.refreshToken ?: throw InvalidRefreshTokenException()

        return api.refreshToken(refreshToken, oAuthConfiguration.clientId).also { res ->
            if (res.code() == 400) {
                withContext(Dispatchers.IO) {
                    res.errorBody()?.source()?.let { errorSource ->
                        val error = errorAdapter.fromJson(errorSource)
                        if (error?.errorCode == ErrorCodes.ERROR_INVALID_GRANT) {
                            clearTokens()
                            throw InvalidRefreshTokenException()
                        }
                    }
                }
            }
        }.getResponseOrThrow()
    }

    override suspend fun logout() {
        tokenPrefs.refreshToken?.let { token ->
            api.revokeToken(token)
        }

        clearTokens()
    }

    private fun clearTokens() {
        tokenPrefs.accessToken = null
        tokenPrefs.refreshToken = null
        tokenPrefs.tokenExpirationTime = null
        prefs.isOnboardingDone = false
    }
}
