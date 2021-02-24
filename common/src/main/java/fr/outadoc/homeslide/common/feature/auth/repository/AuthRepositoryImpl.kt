/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.outadoc.homeslide.common.feature.auth.repository

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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AuthRepositoryImpl(
    private val tokenPrefs: TokenPreferenceRepository,
    private val prefs: GlobalPreferenceRepository,
    private val oAuthConfiguration: OAuthConfiguration,
    private val api: AuthApi,
    private val json: Json
) : AuthRepository {

    override suspend fun getToken(code: String): Token {
        return api.getToken(code, oAuthConfiguration.clientId).getResponseOrThrow()
    }

    override suspend fun refreshToken(): Token {
        val refreshToken = tokenPrefs.refreshToken ?: throw InvalidRefreshTokenException()

        return api.refreshToken(refreshToken, oAuthConfiguration.clientId).also { res ->
            if (res.code() == 400) {
                @Suppress("BlockingMethodInNonBlockingContext")
                withContext(Dispatchers.IO) {
                    res.errorBody()?.string()?.let { errorSource ->
                        val error = json.decodeFromString<AuthError?>(errorSource)
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
