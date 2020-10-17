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

package fr.outadoc.homeslide.app.controlprovider.service

import android.os.Build
import android.service.controls.Control
import android.service.controls.ControlsProviderService
import android.service.controls.actions.ControlAction
import androidx.annotation.RequiresApi
import fr.outadoc.homeslide.app.controlprovider.vm.ControlsProviderViewModel
import java.util.concurrent.Flow
import java.util.function.Consumer
import kotlinx.coroutines.jdk9.asPublisher
import org.koin.android.ext.android.inject

@RequiresApi(Build.VERSION_CODES.R)
class AppControlsProviderService : ControlsProviderService() {

    private val vm: ControlsProviderViewModel by inject()

    override fun createPublisherForAllAvailable(): Flow.Publisher<Control> {
        return vm.getControls(this).asPublisher()
    }

    override fun createPublisherFor(controlIds: List<String>): Flow.Publisher<Control> {
        vm.getControlsWithState(this, controlIds)
        return vm.publisher
    }

    override fun performControlAction(
        controlId: String,
        action: ControlAction,
        consumer: Consumer<Int>
    ) {
        vm.performAction(this, controlId, action)
        consumer.accept(ControlAction.RESPONSE_OK)
    }
}
