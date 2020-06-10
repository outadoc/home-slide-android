package fr.outadoc.homeslide.common.feature.auth.repository

import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.hassapi.api.AuthApi
import fr.outadoc.homeslide.hassapi.model.auth.Token
import fr.outadoc.homeslide.hassapi.repository.AuthRepository
import fr.outadoc.homeslide.rest.auth.OAuthConfiguration
import fr.outadoc.homeslide.rest.util.wrapResponse

class AuthRepositoryImpl(
    private val prefs: TokenPreferenceRepository,
    private val oAuthConfiguration: OAuthConfiguration,
    private val api: AuthApi
) : AuthRepository {

    override suspend fun getToken(code: String): Result<Token> {
        return wrapResponse { api.getToken(code, oAuthConfiguration.clientId) }
    }

    override suspend fun refreshToken(): Result<Token> {
        val refreshToken = prefs.refreshToken
            ?: return Result.failure(Exception("No refresh token available"))

        return wrapResponse { api.refreshToken(refreshToken, oAuthConfiguration.clientId) }
    }
}