package fr.outadoc.quickhass.shortcuts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.outadoc.quickhass.model.Shortcut
import fr.outadoc.quickhass.model.State
import fr.outadoc.quickhass.rest.HomeAssistantServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class QuickAccessViewModel : ViewModel() {

    private val server = HomeAssistantServer()

    private val _shortcuts: MutableLiveData<List<Shortcut>> = MutableLiveData()

    val shortcuts: LiveData<List<Shortcut>>
        get() = _shortcuts

    fun loadShortcuts() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = server.getStates()

            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        _shortcuts.value = response.body()?.toShortcuts() ?: emptyList()
                    }
                } catch (e: HttpException) {
                    e.printStackTrace()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun List<State>.toShortcuts(): List<Shortcut> =
        map { state ->
            Shortcut(
                state.attributes.friendlyName ?: state.entityId,
                state.attributes.icon?.replace("mdi:", "")
            )
        }
}