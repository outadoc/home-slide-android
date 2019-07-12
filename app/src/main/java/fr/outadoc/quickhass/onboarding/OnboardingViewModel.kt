package fr.outadoc.quickhass.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fr.outadoc.quickhass.model.DiscoveryInfo
import fr.outadoc.quickhass.preferences.PreferenceRepositoryImpl
import fr.outadoc.quickhass.rest.InstanceRepositoryImpl

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferenceRepositoryImpl(application.applicationContext)
    private val repository = InstanceRepositoryImpl(prefs)

    private val _instanceDiscoveryInfo = MutableLiveData<Result<DiscoveryInfo>>()
    val instanceDiscoveryInfo: LiveData<Result<DiscoveryInfo>> = _instanceDiscoveryInfo

    fun onInstanceUrlFieldChanged(instanceUrl: String) {

    }
}