package fr.outadoc.homeslide.app.onboarding.vm

import android.os.Handler
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.Timber
import fr.outadoc.homeslide.app.onboarding.model.CallStatus
import fr.outadoc.homeslide.app.onboarding.model.DiscoveryInfo
import fr.outadoc.homeslide.app.onboarding.model.NavigationFlow
import fr.outadoc.homeslide.app.onboarding.model.ZeroconfHost
import fr.outadoc.homeslide.app.onboarding.rest.DiscoveryRepository
import fr.outadoc.homeslide.shared.lifecycle.Event
import fr.outadoc.homeslide.shared.preferences.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@UseExperimental(ExperimentalStdlibApi::class)
class HostSetupViewModel(
    private val prefs: PreferenceRepository,
    private val repository: DiscoveryRepository,
    private val zeroconfDiscoveryService: fr.outadoc.homeslide.zeroconf.ZeroconfDiscoveryService
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
                Timber.e(e)
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