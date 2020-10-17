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
