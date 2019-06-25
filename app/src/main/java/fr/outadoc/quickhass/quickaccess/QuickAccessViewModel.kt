package fr.outadoc.quickhass.quickaccess

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.model.Entity
import fr.outadoc.quickhass.model.EntityFactory
import fr.outadoc.quickhass.rest.HomeAssistantServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class QuickAccessViewModel : ViewModel() {

    private val server = HomeAssistantServer()

    private val _shortcuts = MutableLiveData<List<Entity>>()
    val shortcuts: LiveData<List<Entity>> = _shortcuts

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> = _error

    fun loadShortcuts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = server.getStates()

                if (response.isSuccessful) {
                    _shortcuts.postValue(response.body()
                        ?.map { EntityFactory.create(it) }
                        ?.filter { it.isVisible }
                        ?.filter { !INITIAL_DOMAIN_BLACKLIST.contains(it.domain) }
                        ?.sortedBy { it.domain }
                        ?: emptyList())
                }
            } catch (e: HttpException) {
                _error.postValue(e)
            } catch (e: IOException) {
                _error.postValue(e)
            }
        }
    }

    fun onEntityClick(item: Entity) {
        if (item.primaryAction == null)
            return

        viewModelScope.launch(Dispatchers.IO) {
            val response = server.callService(item.primaryAction as Action)

            if (response.isSuccessful) {
                loadShortcuts()
            }
        }

    }

    companion object {
        val INITIAL_DOMAIN_BLACKLIST = listOf(
            "automation",
            "device_tracker",
            "updater",
            "camera",
            "persistent_notification"
        )
    }
}