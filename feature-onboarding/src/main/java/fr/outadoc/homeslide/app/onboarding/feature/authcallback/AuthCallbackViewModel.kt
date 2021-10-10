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

package fr.outadoc.homeslide.app.onboarding.feature.authcallback

import fr.outadoc.homeslide.app.onboarding.navigation.NavigationEvent
import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.hassapi.model.auth.Token
import fr.outadoc.homeslide.hassapi.model.auth.expiresInDuration
import fr.outadoc.homeslide.hassapi.model.discovery.Config
import fr.outadoc.homeslide.hassapi.repository.AuthRepository
import fr.outadoc.homeslide.hassapi.repository.InstanceConfigRepository
import fr.outadoc.homeslide.logging.KLog
import io.uniflow.android.AndroidDataFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class AuthCallbackViewModel(
    private val tokenPrefs: TokenPreferenceRepository,
    private val authRepository: AuthRepository,
    private val configRepository: InstanceConfigRepository,
    private val urlPreferenceRepository: UrlPreferenceRepository,
    private val clock: Clock
) : AndroidDataFlow() {

    fun onAuthCallback(code: String) = action {
        KLog.d { "received authentication code, fetching token" }

        withContext(Dispatchers.IO) {
            try {
                authRepository.getToken(code).saveTokenToPrefs()
            } catch (e: Exception) {
                KLog.e(e) { "couldn't retrieve token using code $code" }
            }

            try {
                configRepository.getConfig().saveInstanceBaseUrlsToPrefs()
            } catch (e: Exception) {
                KLog.e(e) { "Error while fetching base URL configuration" }
            }

            sendEvent { NavigationEvent.Next }
        }
    }

    private fun Token.saveTokenToPrefs() {
        tokenPrefs.accessToken = accessToken
        tokenPrefs.refreshToken = refreshToken
        tokenPrefs.tokenExpirationTime = clock.now() + expiresInDuration
    }

    private fun Config.saveInstanceBaseUrlsToPrefs() {
        if (!localBaseUrl.isNullOrEmpty() || !remoteBaseUrl.isNullOrEmpty() || !baseUrl.isNullOrEmpty()) {
            urlPreferenceRepository.localInstanceBaseUrl = localBaseUrl ?: baseUrl
            urlPreferenceRepository.remoteInstanceBaseUrl = remoteBaseUrl ?: baseUrl
        }
    }
}
