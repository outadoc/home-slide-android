package fr.outadoc.homeslide.app.onboarding.feature.host

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import fr.outadoc.homeslide.app.onboarding.feature.host.model.CallStatus
import fr.outadoc.homeslide.app.onboarding.model.NavigationFlow
import fr.outadoc.homeslide.app.onboarding.feature.host.model.ZeroconfHost
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.hassapi.repository.DiscoveryRepository
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.rest.auth.OAuthConfiguration
import fr.outadoc.homeslide.util.lifecycle.Event
import fr.outadoc.homeslide.util.sanitizeUrl
import fr.outadoc.homeslide.zeroconf.ZeroconfDiscoveryService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@OptIn(FlowPreview::class, ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class HostSetupViewModel(
    private val urlPrefs: UrlPreferenceRepository,
    private val repository: DiscoveryRepository,
    private val oAuthConfiguration: OAuthConfiguration,
    private val zeroconfDiscoveryService: ZeroconfDiscoveryService
) : ViewModel() {

    private val _autoDiscoveredInstances = MutableLiveData<List<ZeroconfHost>>()
    val autoDiscoveredInstances: LiveData<List<ZeroconfHost>> = _autoDiscoveredInstances

    private val _navigateTo = MutableLiveData<Event<NavigationFlow>>()
    val navigateTo: LiveData<Event<NavigationFlow>> = _navigateTo

    private val _inputInstanceUrl = MutableLiveData<String>()
    val inputInstanceUrl =
        _inputInstanceUrl
            .asFlow()
            .onStart {
                val knownUrl = urlPrefs.instanceBaseUrl
                if (knownUrl != null) {
                    emit(knownUrl)
                } else {
                    emit(DEFAULT_INSTANCE_URL)
                }
            }
            .distinctUntilChanged()
            .asLiveData()

    private val targetInstanceUrl =
        inputInstanceUrl
            .asFlow()
            .debounce(UPDATE_TIME_INTERVAL_MS)
            .map { instanceUrl -> instanceUrl.sanitizeUrl() }
            .distinctUntilChanged()

    private val instanceDiscoveryInfoFlow =
        targetInstanceUrl
            .flatMapConcat { instanceUrl ->
                flow {
                    emit(CallStatus.Loading to null)
                    emit(
                        CallStatus.Done(
                            repository.getDiscoveryInfo(
                                instanceUrl ?: ""
                            )
                        ) to instanceUrl
                    )
                }
            }

    private var validatedTargetInstanceUrl: String? = null
    val canContinue =
        instanceDiscoveryInfoFlow
            .onEach { (_, url) ->
                validatedTargetInstanceUrl = url
            }
            .map { (result, _) -> result is CallStatus.Done && result.value.isSuccess }
            .onStart { emit(false) }
            .asLiveData()

    val instanceDiscoveryInfo =
        instanceDiscoveryInfoFlow.map { it.first }.asLiveData()

    private val authenticationPageUrl: Uri?
        get() = urlPrefs.instanceBaseUrl?.let {
            it.toUri()
                .buildUpon()
                .appendPath("auth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", oAuthConfiguration.clientId)
                .appendQueryParameter("redirect_uri", oAuthConfiguration.redirectUri)
                .build()
        }

    init {
        zeroconfDiscoveryService.setOnServiceDiscoveredListener { serviceInfo ->
            try {
                val host = with(serviceInfo) {
                    ZeroconfHost(
                        hostName = "${host.hostAddress}:$port",
                        baseUrl = attributes["base_url"]?.decodeToString(),
                        version = attributes["version"]?.decodeToString(),
                        instanceName = serviceName
                    )
                }

                val alreadyExists =
                    _autoDiscoveredInstances.value?.any { it.hostName == host.hostName } ?: false

                if (!alreadyExists) {
                    _autoDiscoveredInstances.postValue(
                        (_autoDiscoveredInstances.value ?: emptyList()) + host
                    )
                }
            } catch (e: Exception) {
                KLog.e(e)
            }
        }
    }

    fun startDiscovery() {
        zeroconfDiscoveryService.startDiscovery()
    }

    fun onInstanceUrlChanged(instanceUrl: String) {
        _inputInstanceUrl.value = instanceUrl
    }

    fun onLoginClicked() {
        if (canContinue.value != true) return

        validatedTargetInstanceUrl?.let { instanceUrl ->
            stopDiscovery()
            urlPrefs.instanceBaseUrl = instanceUrl
            startAuthFlow()
        }
    }

    fun onZeroconfHostSelected(zeroconfHost: ZeroconfHost) {
        stopDiscovery()

        urlPrefs.instanceBaseUrl = "http://${zeroconfHost.hostName}"
        urlPrefs.altInstanceBaseUrl = zeroconfHost.baseUrl

        startAuthFlow()
    }

    fun stopDiscovery() {
        zeroconfDiscoveryService.stopDiscovery()
    }

    private fun startAuthFlow() {
        authenticationPageUrl?.let { url ->
            _navigateTo.value = Event(NavigationFlow.Url(url))
        }
    }

    companion object {
        private const val DEFAULT_INSTANCE_URL = "http://hassio.local:8123"
        private const val UPDATE_TIME_INTERVAL_MS = 700L
    }
}
