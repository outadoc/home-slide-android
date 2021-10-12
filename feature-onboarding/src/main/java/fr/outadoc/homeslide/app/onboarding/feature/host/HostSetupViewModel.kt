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
import fr.outadoc.homeslide.common.extensions.actionWith
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.hassapi.repository.DiscoveryRepository
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.rest.auth.OAuthConfiguration
import fr.outadoc.homeslide.rest.auth.OAuthConstants.PARAM_CLIENT_ID
import fr.outadoc.homeslide.rest.auth.OAuthConstants.PARAM_REDIRECT_URI
import fr.outadoc.homeslide.util.sanitizeUrl
import fr.outadoc.homeslide.zeroconf.ZeroconfDiscoveryService
import io.uniflow.android.AndroidDataFlow
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
) : AndroidDataFlow(
    State.Initial(
        selectedInstanceUrl = "",
        discoveredInstances = emptySet(),
        ignoreTlsErrors = urlPrefs.ignoreTlsErrors
    )
) {
    sealed class State(
        open val discoveredInstances: Set<ZeroconfHost>,
        open val selectedInstanceUrl: String,
        open val ignoreTlsErrors: Boolean
    ) : UIState() {

        data class Initial(
            override val discoveredInstances: Set<ZeroconfHost>,
            override val selectedInstanceUrl: String,
            override val ignoreTlsErrors: Boolean
        ) : State(discoveredInstances, selectedInstanceUrl, ignoreTlsErrors)

        data class Loading(
            override val discoveredInstances: Set<ZeroconfHost>,
            override val selectedInstanceUrl: String,
            override val ignoreTlsErrors: Boolean
        ) : State(discoveredInstances, selectedInstanceUrl, ignoreTlsErrors)

        data class Error(
            override val discoveredInstances: Set<ZeroconfHost>,
            override val selectedInstanceUrl: String,
            override val ignoreTlsErrors: Boolean,
            val exception: Exception?
        ) : State(discoveredInstances, selectedInstanceUrl, ignoreTlsErrors)

        data class Success(
            override val selectedInstanceUrl: String,
            override val discoveredInstances: Set<ZeroconfHost>,
            override val ignoreTlsErrors: Boolean
        ) : State(discoveredInstances, selectedInstanceUrl, ignoreTlsErrors)
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
                actionWith<State> { currentState ->
                    setState { currentState.withDiscoveredInstance(discovered) }
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

    fun onOpen() = actionWith<State.Initial> {
        sendEvent {
            Event.SetInstanceUrl(urlPrefs.localInstanceBaseUrl ?: DEFAULT_INSTANCE_URL)
        }
    }

    fun startDiscovery() = action {
        zeroconfDiscoveryService.startDiscovery()
    }

    fun onInstanceUrlChanged(instanceUrl: String) = action {
        instanceUrlChannel.send(instanceUrl)
    }

    private fun probeUrl(instanceUrl: String) = actionWith<State> { currentState ->
        if (instanceUrl.isBlank()) {
            setState { currentState.toInitialState() }
            return@actionWith
        }

        setState { currentState.toLoadingState(instanceUrl) }

        val nextState = instanceUrl.sanitizeUrl()
            ?.let { sanitizedUrl ->
                try {
                    val isReachable = repository.isInstanceReachable(sanitizedUrl)
                    if (isReachable) {
                        currentState.toSuccessState(instanceUrl)
                    } else {
                        currentState.toErrorState(
                            instanceUrl,
                            DiscoveryException(
                                hostSetupResourceProvider.invalidDiscoveryInfoMessage
                            )
                        )
                    }
                } catch (e: Exception) {
                    KLog.e(e) { "Exception thrown during probe" }
                    // Error during discovery
                    currentState.toErrorState(
                        instanceUrl,
                        DiscoveryException(
                            hostSetupResourceProvider.invalidDiscoveryInfoMessage, e
                        )
                    )
                }
            } ?: currentState.toErrorState(instanceUrl)

        setState { nextState }
    }

    fun onLoginClicked() = action { currentState ->
        when (currentState) {
            is State.Success -> {
                stopDiscovery()
                saveAndProceed(
                    localBaseUrl = currentState.selectedInstanceUrl
                )
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

    fun onIgnoreTlsErrorsChanged(checked: Boolean) = actionWith<State> { currentState ->
        urlPrefs.ignoreTlsErrors = checked

        if (currentState.ignoreTlsErrors == checked) return@actionWith

        setState { currentState.withIgnoreTlsErrors(checked) }
        instanceUrlChannel.send(currentState.selectedInstanceUrl)
    }

    fun onZeroconfHostSelected(zeroconfHost: ZeroconfHost) = action {
        stopDiscovery()
        saveAndProceed(
            localBaseUrl = zeroconfHost.localBaseUrl
        )
    }

    private fun saveAndProceed(localBaseUrl: String) = action {
        urlPrefs.apply {
            localInstanceBaseUrl = localBaseUrl
        }

        localBaseUrl.toUri()
            .toAuthenticationPageUrl()
            ?.let { url ->
                sendEvent { NavigationEvent.Url(url) }
            }
    }

    private fun State.withDiscoveredInstance(instance: ZeroconfHost): State {
        val newInstances = discoveredInstances + instance
        return when (this) {
            is State.Initial -> copy(discoveredInstances = newInstances)
            is State.Loading -> copy(discoveredInstances = newInstances)
            is State.Error -> copy(discoveredInstances = newInstances)
            is State.Success -> copy(discoveredInstances = newInstances)
        }
    }

    private fun State.toInitialState() =
        State.Initial(
            selectedInstanceUrl = "",
            discoveredInstances = discoveredInstances,
            ignoreTlsErrors = ignoreTlsErrors
        )

    private fun State.toLoadingState(instanceUrl: String) =
        State.Loading(
            selectedInstanceUrl = instanceUrl,
            discoveredInstances = discoveredInstances,
            ignoreTlsErrors = ignoreTlsErrors
        )

    private fun State.toSuccessState(instanceUrl: String) =
        State.Success(
            selectedInstanceUrl = instanceUrl,
            discoveredInstances = discoveredInstances,
            ignoreTlsErrors = ignoreTlsErrors
        )

    private fun State.toErrorState(instanceUrl: String, exception: Exception? = null) =
        State.Error(
            selectedInstanceUrl = instanceUrl,
            discoveredInstances = discoveredInstances,
            ignoreTlsErrors = ignoreTlsErrors,
            exception = exception
        )

    private fun State.withIgnoreTlsErrors(ignoreTlsErrors: Boolean) =
        when (this) {
            is State.Initial -> copy(ignoreTlsErrors = ignoreTlsErrors)
            is State.Loading -> copy(ignoreTlsErrors = ignoreTlsErrors)
            is State.Error -> copy(ignoreTlsErrors = ignoreTlsErrors)
            is State.Success -> copy(ignoreTlsErrors = ignoreTlsErrors)
        }

    fun stopDiscovery() {
        zeroconfDiscoveryService.stopDiscovery()
    }

    override fun onCleared() {
        stopDiscovery()
        super.onCleared()
    }

    companion object {
        private const val DEFAULT_INSTANCE_URL = "http://hassio.local:8123"
        private const val UPDATE_TIME_INTERVAL_MS = 700L
    }
}
