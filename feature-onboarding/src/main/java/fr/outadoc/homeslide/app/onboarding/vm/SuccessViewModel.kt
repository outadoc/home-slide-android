package fr.outadoc.homeslide.app.onboarding.vm

import android.app.ActivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.outadoc.homeslide.app.onboarding.model.NavigationFlow
import fr.outadoc.homeslide.common.preferences.PreferenceRepository
import fr.outadoc.homeslide.util.lifecycle.Event

class SuccessViewModel(
    private val prefs: PreferenceRepository,
    private val activityManager: ActivityManager
) : ViewModel() {

    private val _navigateTo = MutableLiveData<Event<NavigationFlow>>()
    val navigateTo: LiveData<Event<NavigationFlow>> = _navigateTo

    val showShowConfetti: Boolean
        get() = !activityManager.isLowRamDevice

    fun onContinueClicked() {
        prefs.isOnboardingDone = true
        _navigateTo.value = Event(NavigationFlow.Next)
    }
}