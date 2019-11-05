package fr.outadoc.quickhass.feature.onboarding.vm

import android.os.Handler
import androidx.core.net.toUri
import androidx.lifecycle.*
import fr.outadoc.quickhass.feature.onboarding.model.CallStatus
import fr.outadoc.quickhass.feature.onboarding.model.DiscoveryInfo
import fr.outadoc.quickhass.feature.onboarding.model.NavigationFlow
import fr.outadoc.quickhass.feature.onboarding.model.ZeroconfHost
import fr.outadoc.quickhass.feature.onboarding.rest.DiscoveryRepository
import fr.outadoc.quickhass.feature.onboarding.rest.ZeroconfDiscoveryService
import fr.outadoc.quickhass.lifecycle.Event
import fr.outadoc.quickhass.preferences.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@UseExperimental(ExperimentalStdlibApi::class)
class HostSetupViewModel(
    private val prefs: PreferenceRepository,
    private val repository: DiscoveryRepository,
    private val zeroconfDiscoveryService: ZeroconfDiscoveryService
) : ViewModel() {

    private val _instanceDiscoveryInfo = MutableLiveData<CallStatus<DiscoveryInfo>>()
    val instanceDiscoveryInfo: LiveData<CallStatus<DiscoveryInfo>> = _instanceDiscoveryInfo

    private val _autoDiscoveredInstances = MutableLiveData<List<ZeroconfHost>>()
    val autoDiscoveredInstances: LiveData<List<ZeroconfHost>> = _autoDiscoveredInstances

    private val _navigateTo = MutableLiveData<Event<NavigationFlow>>()
    val navigateTo: LiveData<Event<NavigationFlow>> = _navigateTo

    val canContinue = instanceDiscoveryInfo.map { it is CallStatus.Done && it.value.isSuccess }

    val defaultInstanceUrl = DEFAULT_INSTANCE_URL

    private var inputInstanceUrl: String? = null
    private var discoveryJob: Job? = null

    private val handler = Handler()

    init {
        zeroconfDiscoveryService.setOnServiceDiscoveredListener { serviceInfo ->
            try {
                val host = with(serviceInfo) {
                    ZeroconfHost(
                        hostName = "${host.hostAddress}:${port}",
                        baseUrl = attributes["base_url"]?.decodeToString(),
                        version = attributes["version"]?.decodeToString(),
                        instanceName = serviceName
                    )
                }

                val alreadyExists = _autoDiscoveredInstances.value?.any { it.hostName == host.hostName } ?: false

                if (!alreadyExists) {
                    _autoDiscoveredInstances.postValue(
                        (_autoDiscoveredInstances.value ?: emptyList()) + host
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startDiscovery() {
        zeroconfDiscoveryService.startDiscovery()
    }

    fun onInstanceUrlChanged(instanceUrl: String) {
        instanceUrl.sanitizeBaseUrl()?.let { sanitizedUrl ->
            inputInstanceUrl = sanitizedUrl

            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({ doOnInstanceUrlChanged(sanitizedUrl) }, UPDATE_TIME_INTERVAL)
        }
    }

    private fun doOnInstanceUrlChanged(instanceUrl: String) {
        discoveryJob?.cancel()
        _instanceDiscoveryInfo.value = CallStatus.Loading

        discoveryJob = viewModelScope.launch(Dispatchers.IO) {
            _instanceDiscoveryInfo.postValue(
                CallStatus.Done(repository.getDiscoveryInfo(instanceUrl))
            )
        }
    }

    fun onContinueClicked() {
        inputInstanceUrl?.let { instanceUrl ->
            if (canContinue.value!!) {
                zeroconfDiscoveryService.stopDiscovery()
                prefs.instanceBaseUrl = instanceUrl
                _navigateTo.value = Event(NavigationFlow.Next)
            }
        }
    }

    fun onZeroconfHostSelected(zeroconfHost: ZeroconfHost) {
        zeroconfDiscoveryService.stopDiscovery()

        prefs.instanceBaseUrl = "http://${zeroconfHost.hostName}"
        prefs.altInstanceBaseUrl = zeroconfHost.baseUrl
        _navigateTo.value = Event(NavigationFlow.Next)
    }

    fun stopDiscovery() {
        zeroconfDiscoveryService.stopDiscovery()
    }

    private fun String?.sanitizeBaseUrl(): String? {
        if (this == null)
            return null

        val str = this
            .trim()
            .ensureProtocol()

        if (str.isEmpty() || str.length < 3) return null

        try {
            str.toUri()
        } catch (ignored: Exception) {
            return null
        }

        if (str.last() == '/') return str.dropLast(1)

        return str
    }

    private fun String.ensureProtocol(): String {
        return when {
            this.startsWith("http://") || this.startsWith("https://") -> this
            else -> "http://$this"
        }
    }

    companion object {
        private const val DEFAULT_INSTANCE_URL = "http://hassio.local:8123"
        private const val UPDATE_TIME_INTERVAL = 500L
    }
}