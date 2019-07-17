package fr.outadoc.quickhass.onboarding

import android.app.Application
import androidx.lifecycle.*
import fr.outadoc.quickhass.lifecycle.Event
import fr.outadoc.quickhass.model.DiscoveryInfo
import fr.outadoc.quickhass.onboarding.model.NavigationFlow
import fr.outadoc.quickhass.onboarding.rest.DiscoveryRepositoryImpl
import fr.outadoc.quickhass.preferences.PreferenceRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HostSetupViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferenceRepositoryImpl(application.applicationContext)
    private val repository = DiscoveryRepositoryImpl()

    private val _instanceDiscoveryInfo = MutableLiveData<Result<DiscoveryInfo>>()
    val instanceDiscoveryInfo: LiveData<Result<DiscoveryInfo>> = _instanceDiscoveryInfo

    private val _navigateTo = MutableLiveData<Event<NavigationFlow>>()
    val navigateTo: LiveData<Event<NavigationFlow>> = _navigateTo

    val canContinue = instanceDiscoveryInfo.map {
        it.isSuccess
    }

    fun onInstanceUrlChange(instanceUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _instanceDiscoveryInfo.postValue(
                repository.getDiscoveryInfo(instanceUrl)
            )
        }
    }

    fun onContinueClicked() {
        if (canContinue.value!!) {
            _navigateTo.value = Event(NavigationFlow.NEXT)
        }
    }

}