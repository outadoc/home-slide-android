package fr.outadoc.homeslide.app.onboarding.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.outadoc.homeslide.app.onboarding.model.NavigationFlow
import fr.outadoc.homeslide.shared.lifecycle.Event

class ShortcutSetupViewModel : ViewModel() {

    private val _navigateTo = MutableLiveData<Event<NavigationFlow>>()
    val navigateTo: LiveData<Event<NavigationFlow>> = _navigateTo

    fun onContinueClicked() {
        _navigateTo.value = Event(NavigationFlow.Next)
    }
}