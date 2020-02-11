package fr.outadoc.homeslide.app.onboarding.vm

import android.net.Uri
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import fr.outadoc.homeslide.app.onboarding.model.CallStatus
import fr.outadoc.homeslide.app.onboarding.model.NavigationFlow
import fr.outadoc.homeslide.common.preferences.PreferenceRepository
import fr.outadoc.homeslide.hassapi.model.discovery.ApiStatus
import fr.outadoc.homeslide.hassapi.repository.DiscoveryRepository
import fr.outadoc.homeslide.util.lifecycle.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LegacyAuthSetupViewModel(
    private val repository: DiscoveryRepository,
    private val prefs: PreferenceRepository
) : ViewModel() {

    private val _apiStatus = MutableLiveData<CallStatus<ApiStatus>>()
    val apiStatus: LiveData<CallStatus<ApiStatus>> = _apiStatus

    private val _navigateTo = MutableLiveData<Event<NavigationFlow>>()
    val navigateTo: LiveData<Event<NavigationFlow>> = _navigateTo

    val canContinue = apiStatus.map { it is CallStatus.Done && it.value.isSuccess }

    private var inputJwt: String? = null
    private var apiStatusJob: Job? = null

    private val handler = Handler()

    fun onTokenChanged(token: String) {
        inputJwt = token

        if (!token.isValidJwt()) {
            _apiStatus.value = CallStatus.Done(Result.failure(InvalidTokenException()))
        }

        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({ doOnTokenChanged(token) }, UPDATE_TIME_INTERVAL)
    }

    private fun doOnTokenChanged(token: String) {
        apiStatusJob?.cancel()
        _apiStatus.value = CallStatus.Loading

        apiStatusJob = viewModelScope.launch(Dispatchers.IO) {
            _apiStatus.postValue(
                CallStatus.Done(repository.getApiStatus(prefs.instanceBaseUrl, token))
            )
        }
    }

    fun onContinueClicked() {
        inputJwt?.let { jwt ->
            if (canContinue.value!!) {
                prefs.accessToken = jwt
                _navigateTo.value = Event(NavigationFlow.Next)
            }
        }
    }

    private fun String.isValidJwt(): Boolean {
        return try {
            !JWT(this).isExpired(0)
        } catch (e: DecodeException) {
            false
        }
    }

    fun onClickHelpLink() {
        val profilePage = Uri.parse(prefs.instanceBaseUrl)
            .buildUpon()
            .appendPath("profile")
            .build()

        _navigateTo.postValue(Event(NavigationFlow.Url(profilePage)))
    }

    companion object {
        private const val UPDATE_TIME_INTERVAL = 500L
    }

    class InvalidTokenException(message: String? = null) : Exception(message)
}