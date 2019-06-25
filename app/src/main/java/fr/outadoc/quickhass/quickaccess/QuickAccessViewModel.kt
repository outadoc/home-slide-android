package fr.outadoc.quickhass.quickaccess

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.outadoc.quickhass.model.Entity
import fr.outadoc.quickhass.model.EntityFactory
import fr.outadoc.quickhass.rest.HomeAssistantServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class QuickAccessViewModel : ViewModel() {

    private val server = HomeAssistantServer()

    private val _shortcuts: MutableLiveData<List<Entity>> = MutableLiveData()

    val shortcuts: LiveData<List<Entity>>
        get() = _shortcuts

    fun loadShortcuts() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = server.getStates()

            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        _shortcuts.value = response.body()
                            ?.map { EntityFactory.create(it) }
                            ?.filter { !it.attributes.hidden }
                            ?.filter { !INITIAL_DOMAIN_BLACKLIST.contains(it.domain) }
                            ?.sortedBy { it.domain }
                            ?: emptyList()
                    }
                } catch (e: HttpException) {
                    e.printStackTrace()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
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