package fr.outadoc.quickhass.slideover

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.model.Entity
import fr.outadoc.quickhass.model.EntityFactory
import fr.outadoc.quickhass.preferences.PreferenceRepositoryImpl
import fr.outadoc.quickhass.rest.HomeAssistantServerImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.*
import kotlin.concurrent.schedule


class EntityGridViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferenceRepositoryImpl(application.applicationContext)
    private val server = HomeAssistantServerImpl(prefs)

    private val _shortcuts = MutableLiveData<List<Entity>>()
    val shortcuts: LiveData<List<Entity>> = _shortcuts

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _shouldAskForInitialValues = MutableLiveData<Boolean>()
    val shouldAskForInitialValues: LiveData<Boolean> = _shouldAskForInitialValues

    private val timer = Timer("Periodic refresh", false)

    fun loadShortcuts() {
        println(prefs.shouldAskForInitialValues)
        if (prefs.shouldAskForInitialValues) {
            _shouldAskForInitialValues.value = prefs.shouldAskForInitialValues
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)

            try {
                val response = server.getStates()

                if (response.isSuccessful) {
                    _shortcuts.postValue(response.body()
                        ?.map { EntityFactory.create(it) }
                        ?.filter { it.isVisible }
                        ?.filter { !INITIAL_DOMAIN_BLACKLIST.contains(it.domain) }
                        ?.sortedBy { it.domain }
                        ?: emptyList())
                } else {
                    _error.postValue(HttpException(response))
                }

            } catch (e: Exception) {
                _error.postValue(e)
            } finally {
                _isLoading.postValue(false)
            }

            timer.schedule(REFRESH_INTERVAL) {
                loadShortcuts()
            }
        }
    }

    fun onEntityClick(item: Entity) {
        if (item.primaryAction == null)
            return

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)

            try {
                val response = server.callService(item.primaryAction as Action)

                if (response.isSuccessful) {
                    loadShortcuts()
                } else {
                    _error.postValue(HttpException(response))
                }

            } catch (e: Exception) {
                _error.postValue(e)
            } finally {
                _isLoading.postValue(false)
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

    companion object {
        const val REFRESH_INTERVAL = 10000L

        val INITIAL_DOMAIN_BLACKLIST = listOf(
            "automation",
            "device_tracker",
            "updater",
            "camera",
            "persistent_notification"
        )
    }
}