package fr.outadoc.homeslide.app.onboarding.feature.authcallback

import fr.outadoc.homeslide.app.onboarding.navigation.NavigationEvent
import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.hassapi.repository.AuthRepository
import fr.outadoc.homeslide.logging.KLog
import io.uniflow.androidx.flow.AndroidDataFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class AuthCallbackViewModel(
    private val tokenPrefs: TokenPreferenceRepository,
    private val authRepository: AuthRepository
) : AndroidDataFlow() {

    fun onAuthCallback(code: String) = action {
        KLog.d { "received authentication code, fetching token" }

        withContext(Dispatchers.IO) {
            authRepository.getToken(code)
                .onSuccess { token ->
                    withContext(Dispatchers.Main) {
                        // Save the auth code
                        tokenPrefs.accessToken = token.accessToken
                        tokenPrefs.refreshToken = token.refreshToken
                        tokenPrefs.tokenExpirationTime = Instant.now().plusSeconds(token.expiresIn)
                    }

                    sendEvent { NavigationEvent.Next }
                }
                .onFailure { e ->
                    KLog.e(e) { "couldn't retrieve token using code $code" }
                }
        }
    }
}