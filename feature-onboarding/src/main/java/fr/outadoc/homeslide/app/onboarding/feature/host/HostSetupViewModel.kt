package fr.outadoc.homeslide.app.onboarding.feature.host

import android.net.Uri
import android.net.nsd.NsdServiceInfo
import androidx.core.net.toUri
import fr.outadoc.homeslide.app.onboarding.feature.host.model.ZeroconfHost
import fr.outadoc.homeslide.app.onboarding.navigation.NavigationEvent
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.hassapi.repository.DiscoveryRepository
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.rest.auth.OAuthConfiguration
import fr.outadoc.homeslide.util.sanitizeUrl
import fr.outadoc.homeslide.zeroconf.ZeroconfDiscoveryService
import io.uniflow.androidx.flow.AndroidDataFlow
import io.uniflow.core.flow.actionOn
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class, ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class HostSetupViewModel(
    private val urlPrefs: UrlPreferenceRepository,
    private val repository: DiscoveryRepository,
    private val oAuthConfiguration: OAuthConfiguration,
    private val zeroconfDiscoveryService: ZeroconfDiscoveryService
) : AndroidDataFlow(State.Initial(urlPrefs.instanceBaseUrl ?: DEFAULT_INSTANCE_URL)) {

    sealed class State(
        open val selectedInstanceUrl: String,
        open val autoDiscoveredInstances: Set<ZeroconfHost> = emptySet()
    ) : UIState() {

        open val canContinue: Boolean = false

        val sanitizedInstanceUrl: String?
            get() = selectedInstanceUrl.sanitizeUrl()

        abstract fun withAutoDiscoveredInstances(instances: Set<ZeroconfHost>): State

        data class Initial(
            override val selectedInstanceUrl: String,
            override val autoDiscoveredInstances: Set<ZeroconfHost> = emptySet()
        ) : State(selectedInstanceUrl) {

            override fun withAutoDiscoveredInstances(instances: Set<ZeroconfHost>) =
                copy(
                    selectedInstanceUrl = selectedInstanceUrl,
                    autoDiscoveredInstances = instances
                )
        }

        data class Loading(
            override val selectedInstanceUrl: String,
            override val autoDiscoveredInstances: Set<ZeroconfHost> = emptySet()
        ) : State(selectedInstanceUrl) {

            override fun withAutoDiscoveredInstances(instances: Set<ZeroconfHost>) =
                copy(
                    selectedInstanceUrl = selectedInstanceUrl,
                    autoDiscoveredInstances = instances
                )
        }

        data class Failure(
            override val selectedInstanceUrl: String,
            override val autoDiscoveredInstances: Set<ZeroconfHost> = emptySet()
        ) : State(selectedInstanceUrl) {

            override fun withAutoDiscoveredInstances(instances: Set<ZeroconfHost>) =
                copy(
                    selectedInstanceUrl = selectedInstanceUrl,
                    autoDiscoveredInstances = instances
                )
        }

        data class Ready(
            override val selectedInstanceUrl: String,
            override val autoDiscoveredInstances: Set<ZeroconfHost> = emptySet()
        ) : State(selectedInstanceUrl) {

            override val canContinue: Boolean = true
            override fun withAutoDiscoveredInstances(instances: Set<ZeroconfHost>) =
                copy(
                    selectedInstanceUrl = selectedInstanceUrl,
                    autoDiscoveredInstances = instances
                )
        }
    }

    sealed class Event : UIEvent() {
        data class SetInstanceUrl(val instanceUrl: String) : Event()
    }

    private fun Uri.toAuthenticationPageUrl() =
        buildUpon()
            .appendPath("auth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", oAuthConfiguration.clientId)
            .appendQueryParameter("redirect_uri", oAuthConfiguration.redirectUri)
            .build()

    init {
        zeroconfDiscoveryService.setOnServiceDiscoveredListener { serviceInfo: NsdServiceInfo ->
            try {
                val discovered = with(serviceInfo) {
                    ZeroconfHost(
                        hostName = "${host.hostAddress}:$port",
                        baseUrl = attributes["base_url"]?.decodeToString(),
                        version = attributes["version"]?.decodeToString(),
                        instanceName = serviceName
                    )
                }

                actionOn<State> { currentState ->
                    setState {
                        currentState.withAutoDiscoveredInstances(
                            currentState.autoDiscoveredInstances + discovered
                        )
                    }
                }
            } catch (e: Exception) {
                KLog.e(e)
            }
        }
    }

    fun onOpen() = actionOn<State.Initial> { currentState ->
        sendEvent { Event.SetInstanceUrl(currentState.selectedInstanceUrl) }
    }

    fun startDiscovery() = action {
        zeroconfDiscoveryService.startDiscovery()
    }

    fun onInstanceUrlChanged(instanceUrl: String) = actionOn<State> { currentState ->
        val nextState = State.Loading(
            selectedInstanceUrl = instanceUrl,
            autoDiscoveredInstances = currentState.autoDiscoveredInstances
        )

        setState { nextState }

        val sanitized = nextState.sanitizedInstanceUrl
        val result = if (sanitized == null) {
            Result.failure(IllegalArgumentException("instanceUrl can't be sanitized"))
        } else {
            repository.getDiscoveryInfo(sanitized)
        }

        setState {
            if (result.isSuccess) {
                State.Ready(
                    selectedInstanceUrl = instanceUrl,
                    autoDiscoveredInstances = nextState.autoDiscoveredInstances
                )
            } else {
                State.Failure(
                    selectedInstanceUrl = instanceUrl,
                    autoDiscoveredInstances = nextState.autoDiscoveredInstances
                )
            }
        }
    }

    fun onLoginClicked() = actionOn<State> { currentState ->
        if (currentState.canContinue) {
            stopDiscovery()

            urlPrefs.instanceBaseUrl = currentState.sanitizedInstanceUrl
            currentState.sanitizedInstanceUrl?.toUri()?.toAuthenticationPageUrl()?.let { url ->
                sendEvent { NavigationEvent.Url(url) }
            }
        }
    }

    fun onZeroconfHostSelected(zeroconfHost: ZeroconfHost) = actionOn<State> { currentState ->
        stopDiscovery()

        urlPrefs.apply {
            instanceBaseUrl = "http://${zeroconfHost.hostName}"
            altInstanceBaseUrl = zeroconfHost.baseUrl
        }

        urlPrefs.instanceBaseUrl?.toUri()?.toAuthenticationPageUrl()?.let { url ->
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
