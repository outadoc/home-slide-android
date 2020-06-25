package fr.outadoc.homeslide.common.log

import fr.outadoc.homeslide.logging.KLog
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.Logger

class UniFlowCustomLogger : Logger {

    override fun log(message: String) = KLog.i { message }

    override fun logState(state: UIState) = KLog.i { "[STATE] - $state" }

    override fun logEvent(event: UIEvent) = KLog.i { "<EVENT> - $event" }

    override fun logError(errorMessage: String, error: Exception?) {
        KLog.e(error) { "<EVENT> - !ERROR! - $errorMessage" }
    }
}