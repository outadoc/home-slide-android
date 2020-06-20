package fr.outadoc.homeslide.common.log

import com.github.ajalt.timberkt.Timber
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.Logger

class UniFlowTimberLogger : Logger {

    override fun log(message: String) {
        Timber.i { message }
    }

    override fun logState(state: UIState) {
        Timber.i { "[STATE] - $state" }
    }

    override fun logEvent(event: UIEvent) {
        Timber.i { "<EVENT> - $event" }
    }

    override fun logError(errorMessage: String, error: Exception?) {
        Timber.e(error) { "<EVENT> - !ERROR! - $errorMessage" }
    }
}