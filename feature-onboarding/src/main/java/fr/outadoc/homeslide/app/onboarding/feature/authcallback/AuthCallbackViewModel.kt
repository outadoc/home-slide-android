package fr.outadoc.homeslide.app.onboarding.feature.authcallback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.outadoc.homeslide.app.onboarding.model.NavigationFlow
import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.hassapi.repository.AuthRepository
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.util.lifecycle.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant

class AuthCallbackViewModel(
    private val tokenPrefs: TokenPreferenceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _navigateTo = MutableLiveData<Event<NavigationFlow>>()
    val navigateTo: LiveData<Event<NavigationFlow>> = _navigateTo

    fun onAuthCallback(code: String) {
        KLog.d { "received authentication code, fetching token" }

        viewModelScope.launch(Dispatchers.IO) {
            authRepository.getToken(code)
                .onSuccess { token ->
                    withContext(Dispatchers.Main) {
                        // Save the auth code
                        tokenPrefs.accessToken = token.accessToken
                        tokenPrefs.refreshToken = token.refreshToken
                        tokenPrefs.tokenExpirationTime = Instant.now().plusSeconds(token.expiresIn)

                        _navigateTo.value = Event(NavigationFlow.Next)
                    }
                }
                .onFailure { e ->
                    KLog.e(e) { "couldn't retrieve token using code $code" }
                }
        }
    }
}