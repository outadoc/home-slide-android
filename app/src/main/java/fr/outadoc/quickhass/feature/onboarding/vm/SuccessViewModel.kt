package fr.outadoc.quickhass.feature.onboarding.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fr.outadoc.quickhass.feature.onboarding.model.NavigationFlow
import fr.outadoc.quickhass.lifecycle.Event
import fr.outadoc.quickhass.preferences.PreferenceRepositoryImpl

class SuccessViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferenceRepositoryImpl(application.applicationContext)

    private val _navigateTo = MutableLiveData<Event<NavigationFlow>>()
    val navigateTo: LiveData<Event<NavigationFlow>> = _navigateTo

    fun onContinueClicked() {
        prefs.isOnboardingDone = true
        _navigateTo.value = Event(NavigationFlow.Next)
    }
}