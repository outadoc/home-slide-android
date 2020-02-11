package fr.outadoc.homeslide.app.onboarding.vm

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.Timber
import fr.outadoc.homeslide.app.onboarding.model.NavigationFlow
import fr.outadoc.homeslide.common.preferences.PreferenceRepository
import fr.outadoc.homeslide.hassapi.repository.AuthRepository
import fr.outadoc.homeslide.rest.auth.OAuthConfiguration
import fr.outadoc.homeslide.util.lifecycle.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthSetupViewModel(
    private val prefs: PreferenceRepository,
    private val repository: AuthRepository,
    private val oAuthConfiguration: OAuthConfiguration
) : ViewModel() {

    private val _navigateTo = MutableLiveData<Event<NavigationFlow>>()
    val navigateTo: LiveData<Event<NavigationFlow>> = _navigateTo

    private val authenticationPageUrl: Uri
        get() = prefs.instanceBaseUrl.toUri().buildUpon()
            .appendPath("auth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", oAuthConfiguration.clientId)
            .appendQueryParameter("redirect_uri", oAuthConfiguration.redirectUri)
            .build()

    fun onSignInClick() {
        _navigateTo.value = Event(NavigationFlow.Url(authenticationPageUrl))
    }

    fun onAuthCallback(code: String) {
        Timber.d { "received authentication code, fetching token" }

        viewModelScope.launch(Dispatchers.IO) {
            repository.getToken(code)
                .onSuccess { token ->
                    withContext(Dispatchers.Main) {
                        // Save the auth code
                        prefs.accessToken = token.accessToken
                        prefs.refreshToken = token.refreshToken

                        _navigateTo.value = Event(NavigationFlow.Next)
                    }
                }
                .onFailure { e ->
                    Timber.e(e) { "couldn't retrieve token using code $code" }
                }
        }
    }
}