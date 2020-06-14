package fr.outadoc.homeslide.app.controlprovider.vm

import android.os.Build
import android.service.controls.Control
import android.service.controls.actions.BooleanAction
import android.service.controls.actions.ControlAction
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.Timber
import fr.outadoc.homeslide.app.controlprovider.repository.ControlRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.jdk9.asPublisher
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.R)
class ControlsProviderViewModel(
    private val controlRepository: ControlRepository
) : ViewModel() {

    private val channel = Channel<Control>()
    val publisher = channel.receiveAsFlow().asPublisher()

    fun getControls(): Flow<Control> {
        return flow {
            controlRepository.getEntities()
                .onFailure { e ->
                    Timber.e(e) { "Error while fetching devices for ControlsProviderService" }
                }
                .onSuccess { controls ->
                    controls
                        .forEach { control ->
                            Timber.d { "Found control ${control.controlId}" }
                            emit(control)
                        }
                }
        }.flowOn(Dispatchers.IO)
    }

    fun getControlsWithState(includeIds: List<String>?) {
        viewModelScope.launch(Dispatchers.IO) {
            controlRepository.getEntitiesWithState()
                .onFailure { e ->
                    Timber.e(e) { "Error while fetching devices for ControlsProviderService" }
                }
                .onSuccess { controls ->
                    controls
                        .filter { control -> includeIds == null || control.controlId in includeIds }
                        .forEach { control ->
                            Timber.d { "Found control ${control.controlId}" }
                            channel.send(control)
                        }
                }
        }
    }


    fun performAction(controlId: String, action: ControlAction) {
        viewModelScope.launch(Dispatchers.IO) {
            when (action) {
                is BooleanAction -> {
                    controlRepository.toggleEntity(controlId)
                }
            }

            // Refresh controls
            getControlsWithState(listOf(controlId))
        }
    }
}