package fr.outadoc.quickhass.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import fr.outadoc.quickhass.preferences.PreferenceRepositoryImpl
import fr.outadoc.quickhass.rest.InstanceRepositoryImpl

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferenceRepositoryImpl(application.applicationContext)
    private val repository = InstanceRepositoryImpl(prefs)

    fun onInstanceUrlFieldChanged(instanceUrl: String) {

    }
}