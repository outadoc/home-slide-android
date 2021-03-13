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

package fr.outadoc.homeslide.app.onboarding.feature.host

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import fr.outadoc.homeslide.app.onboarding.feature.host.model.ZeroconfHost
import fr.outadoc.homeslide.app.onboarding.navigation.NavigationEvent
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.hassapi.model.discovery.DiscoveryInfo
import fr.outadoc.homeslide.hassapi.repository.DiscoveryRepository
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.rest.auth.OAuthConfiguration
import fr.outadoc.homeslide.rest.auth.OAuthConstants.PARAM_CLIENT_ID
import fr.outadoc.homeslide.rest.auth.OAuthConstants.PARAM_REDIRECT_URI
import fr.outadoc.homeslide.util.sanitizeUrl
import fr.outadoc.homeslide.zeroconf.ZeroconfDiscoveryService
import io.uniflow.androidx.flow.AndroidDataFlow
import io.uniflow.core.flow.actionOn
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class HostSetupViewModel(
    private val urlPrefs: UrlPreferenceRepository,
    private val repository: DiscoveryRepository,
    private val oAuthConfiguration: OAuthConfiguration,
    private val zeroconfDiscoveryService: ZeroconfDiscoveryService<ZeroconfHost>,
    private val hostSetupResourceProvider: HostSetupResourceProvider
) : AndroidDataFlow(State.Initial(discoveredInstances = emptySet())) {

    sealed class State(
        open val discoveredInstances: Set<ZeroconfHost>
    ) : UIState() {

        data class Initial(override val discoveredInstances: Set<ZeroconfHost>) :
            State(discoveredInstances)

        data class Loading(
            val selectedInstanceUrl: String,
            override val discoveredInstances: Set<ZeroconfHost>
        ) : State(discoveredInstances)

        data class Error(
            override val discoveredInstances: Set<ZeroconfHost>,
            val exception: Exception?
        ) : State(discoveredInstances)

        data class Success(
            val discoveryInfo: DiscoveryInfo,
            override val discoveredInstances: Set<ZeroconfHost>
        ) : State(discoveredInstances)
    }

    sealed class Event : UIEvent() {
        data class SetInstanceUrl(val instanceUrl: String) : Event()
        data class DisplayErrorModal(val message: String?) : Event()
    }

    private fun Uri.toAuthenticationPageUrl() =
        buildUpon()
            .appendPath("auth")
            .appendPath("authorize")
            .appendQueryParameter(PARAM_CLIENT_ID, oAuthConfiguration.clientId)
            .appendQueryParameter(PARAM_REDIRECT_URI, oAuthConfiguration.redirectUri)
            .build()

    private val instanceUrlChannel = Channel<String>()

    init {
        zeroconfDiscoveryService.setOnServiceDiscoveredListener { discovered: ZeroconfHost ->
            try {
                actionOn<State> { currentState ->
                    setState {
                        with(currentState) {
                            val newInstances = discoveredInstances + discovered
                            when (this) {
                                is State.Initial -> copy(discoveredInstances = newInstances)
                                is State.Loading -> copy(discoveredInstances = newInstances)
                                is State.Error -> copy(discoveredInstances = newInstances)
                                is State.Success -> copy(discoveredInstances = newInstances)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                KLog.e(e)
            }
        }

        viewModelScope.launch {
            instanceUrlChannel.consumeAsFlow()
                .debounce(UPDATE_TIME_INTERVAL_MS)
                .collect { instanceUrl ->
                    action {
                        probeUrl(instanceUrl)
                    }
                }
        }
    }

    fun onOpen() = actionOn<State.Initial> {
        sendEvent {
            Event.SetInstanceUrl(urlPrefs.localInstanceBaseUrl ?: DEFAULT_INSTANCE_URL)
        }
    }

    fun startDiscovery() = action {
        zeroconfDiscoveryService.startDiscovery()
    }

    fun onInstanceUrlChanged(instanceUrl: String) = actionOn<State> {
        instanceUrlChannel.send(instanceUrl)
    }

    private fun probeUrl(instanceUrl: String) = actionOn<State> { currentState ->
        if (instanceUrl.isBlank()) {
            setState {
                State.Initial(discoveredInstances = currentState.discoveredInstances)
            }
            return@actionOn
        }

        setState {
            State.Loading(
                selectedInstanceUrl = instanceUrl,
                discoveredInstances = currentState.discoveredInstances
            )
        }

        instanceUrl.sanitizeUrl()?.let { sanitizedUrl ->
            try {
                val discoveryInfo = repository.getDiscoveryInfo(sanitizedUrl)
                setState {
                    State.Success(
                        discoveryInfo = discoveryInfo,
                        discoveredInstances = currentState.discoveredInstances
                    )
                }
            } catch (e: Exception) {
                setState {
                    // Error during discovery
                    State.Error(
                        discoveredInstances = currentState.discoveredInstances,
                        exception = DiscoveryException(
                            hostSetupResourceProvider.invalidDiscoveryInfoMessage,
                            e
                        )
                    )
                }
            }
        } ?: setState {
            // URL can't be sanitized
            State.Error(
                discoveredInstances = currentState.discoveredInstances,
                exception = null
            )
        }
    }

    fun onLoginClicked() = actionOn<State> { currentState ->
        when (currentState) {
            is State.Success -> {
                stopDiscovery()

                with(currentState.discoveryInfo) {
                    saveAndProceed(
                        localBaseUrl = localBaseUrl ?: baseUrl ?: throw DiscoveryException(
                            hostSetupResourceProvider.invalidDiscoveryInfoMessage
                        ),
                        remoteBaseUrl = remoteBaseUrl ?: baseUrl
                    )
                }
            }
            is State.Error -> sendEvent {
                val message = listOf(
                    currentState.exception?.message,
                    currentState.exception?.cause?.message
                ).joinToString(separator = "\n\n")

                Event.DisplayErrorModal(message)
            }
            else -> Unit
        }
    }

    fun onZeroconfHostSelected(zeroconfHost: ZeroconfHost) = actionOn<State> {
        stopDiscovery()
        saveAndProceed(
            localBaseUrl = zeroconfHost.localBaseUrl,
            remoteBaseUrl = zeroconfHost.remoteBaseUrl
        )
    }

    private fun saveAndProceed(localBaseUrl: String, remoteBaseUrl: String?) = action {
        urlPrefs.apply {
            localInstanceBaseUrl = localBaseUrl
            remoteInstanceBaseUrl = remoteBaseUrl
        }

        // Remote should be accessible from anywhere, so use that by default
        val urlToUseForAuth = remoteBaseUrl ?: localBaseUrl
        urlToUseForAuth.toUri()
            .toAuthenticationPageUrl()
            ?.let { url ->
                sendEvent { NavigationEvent.Url(url) }
            }
    }

    fun stopDiscovery() {
        zeroconfDiscoveryService.stopDiscovery()
    }

    companion object {
        private const val DEFAULT_INSTANCE_URL = "http://hassio.local:8123"
        private const val UPDATE_TIME_INTERVAL_MS = 700L
    }
}
