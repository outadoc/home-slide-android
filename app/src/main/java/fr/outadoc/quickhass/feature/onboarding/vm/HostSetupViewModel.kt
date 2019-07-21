package fr.outadoc.quickhass.feature.onboarding.vm

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.*
import fr.outadoc.quickhass.feature.onboarding.model.DiscoveryInfo
import fr.outadoc.quickhass.feature.onboarding.model.NavigationFlow
import fr.outadoc.quickhass.feature.onboarding.rest.DiscoveryRepositoryImpl
import fr.outadoc.quickhass.lifecycle.Event
import fr.outadoc.quickhass.preferences.PreferenceRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HostSetupViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferenceRepositoryImpl(application.applicationContext)
    private val repository = DiscoveryRepositoryImpl()

    private val _instanceDiscoveryInfo = MutableLiveData<Result<DiscoveryInfo>>()
    val instanceDiscoveryInfo: LiveData<Result<DiscoveryInfo>> = _instanceDiscoveryInfo

    private val _navigateTo = MutableLiveData<Event<NavigationFlow>>()
    val navigateTo: LiveData<Event<NavigationFlow>> = _navigateTo

    val canContinue = instanceDiscoveryInfo.map { it.isSuccess }

    private var inputInstanceUrl: String? = null
    private var discoveryJob: Job? = null

    fun onInstanceUrlChanged(instanceUrl: String) {
        instanceUrl.sanitizeBaseUrl()?.let { sanitizedUrl ->
            inputInstanceUrl = sanitizedUrl

            discoveryJob?.cancel()
            discoveryJob = viewModelScope.launch(Dispatchers.IO) {
                _instanceDiscoveryInfo.postValue(
                    repository.getDiscoveryInfo(sanitizedUrl)
                )
            }
        }
    }

    fun onContinueClicked() {
        inputInstanceUrl?.let { instanceUrl ->
            if (canContinue.value!!) {
                prefs.instanceBaseUrl = instanceUrl
                _navigateTo.value = Event(NavigationFlow.Next)
            }
        }
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
}