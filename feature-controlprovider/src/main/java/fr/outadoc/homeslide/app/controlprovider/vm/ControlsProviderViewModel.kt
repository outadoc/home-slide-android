/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.outadoc.homeslide.app.controlprovider.vm

import android.content.Context
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

    fun getControls(context: Context): Flow<Control> {
        return flow {
            try {
                controlRepository.getEntities(context)
                    .forEach { control ->
                        KLog.d { "Found control ${control.controlId}" }
                        emit(control)
                    }
            } catch (e: Exception) {
                KLog.e(e) { "Error while fetching devices for ControlsProviderService" }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getControlsWithState(context: Context, includeIds: List<String>?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                controlRepository.getEntitiesWithState(context)
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

    fun performAction(context: Context, controlId: String, action: ControlAction) {
        viewModelScope.launch(Dispatchers.IO) {
            when (action) {
                is BooleanAction -> controlRepository.toggleEntity(controlId)
            }

            // Refresh controls
            getControlsWithState(context, listOf(controlId))
        }
    }
}
