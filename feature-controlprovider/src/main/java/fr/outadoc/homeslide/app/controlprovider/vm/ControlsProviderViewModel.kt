package fr.outadoc.homeslide.app.controlprovider.vm

import android.os.Build
import android.service.controls.Control
import android.service.controls.actions.BooleanAction
import android.service.controls.actions.ControlAction
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.outadoc.homeslide.app.controlprovider.repository.ControlRepository
import fr.outadoc.homeslide.logging.KLog
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
            try {
                controlRepository.getEntities()
                    .forEach { control ->
                        KLog.d { "Found control ${control.controlId}" }
                        emit(control)
                    }
            } catch (e: Exception) {
                KLog.e(e) { "Error while fetching devices for ControlsProviderService" }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getControlsWithState(includeIds: List<String>?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                controlRepository.getEntitiesWithState()
                    .filter { control -> includeIds == null || control.controlId in includeIds }
                    .forEach { control ->
                        KLog.d { "Found control ${control.controlId}" }
                        channel.send(control)
                    }
            } catch (e: Exception) {
                KLog.e(e) { "Error while fetching devices for ControlsProviderService" }
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
