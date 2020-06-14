package fr.outadoc.homeslide.app.controlprovider.service

import android.os.Build
import android.service.controls.Control
import android.service.controls.ControlsProviderService
import android.service.controls.actions.ControlAction
import androidx.annotation.RequiresApi
import fr.outadoc.homeslide.app.controlprovider.vm.ControlsProviderViewModel
import kotlinx.coroutines.jdk9.asPublisher
import org.koin.android.ext.android.inject
import java.util.concurrent.Flow
import java.util.function.Consumer

@RequiresApi(Build.VERSION_CODES.R)
class AppControlsProviderService : ControlsProviderService() {

    private val vm: ControlsProviderViewModel by inject()

    override fun createPublisherForAllAvailable(): Flow.Publisher<Control> {
        return vm.getControls().asPublisher()
    }

    override fun createPublisherFor(controlIds: List<String>): Flow.Publisher<Control> {
        vm.getControlsWithState(controlIds)
        return vm.publisher
    }

    override fun performControlAction(
        controlId: String,
        action: ControlAction,
        consumer: Consumer<Int>
    ) {
        vm.performAction(controlId, action)
        consumer.accept(ControlAction.RESPONSE_OK)
    }
}