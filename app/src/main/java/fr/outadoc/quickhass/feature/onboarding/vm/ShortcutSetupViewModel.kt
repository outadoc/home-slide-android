package fr.outadoc.quickhass.feature.onboarding.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.outadoc.quickhass.feature.onboarding.model.NavigationFlow
import fr.outadoc.quickhass.lifecycle.Event


class ShortcutSetupViewModel : ViewModel() {

    private val _navigateTo = MutableLiveData<Event<NavigationFlow>>()
    val navigateTo: LiveData<Event<NavigationFlow>> = _navigateTo

    fun onContinueClicked() {
        _navigateTo.value = Event(NavigationFlow.Next)
    }
}