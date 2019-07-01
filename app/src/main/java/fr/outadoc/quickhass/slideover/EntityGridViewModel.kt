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
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


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

    private var threadPoolExecutor: ScheduledExecutorService? = null

    fun loadShortcuts() {
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

            scheduleRefresh()
        }
    }

    private fun scheduleRefresh() {
        val executor = threadPoolExecutor
        if (executor == null || executor.isShutdown) {
            threadPoolExecutor = Executors.newSingleThreadScheduledExecutor()
        }

        threadPoolExecutor?.schedule({
            loadShortcuts()
        }, REFRESH_INTERVAL_S, TimeUnit.SECONDS)
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
        threadPoolExecutor?.shutdown()
        threadPoolExecutor = null
    }

    companion object {
        const val REFRESH_INTERVAL_S = 10L

        val INITIAL_DOMAIN_BLACKLIST = listOf(
            "automation",
            "device_tracker",
            "updater",
            "camera",
            "persistent_notification"
        )
    }
}