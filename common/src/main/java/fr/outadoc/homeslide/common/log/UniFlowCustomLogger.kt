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

package fr.outadoc.homeslide.common.log

import fr.outadoc.homeslide.logging.KLog
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.logger.Logger

class UniFlowCustomLogger : Logger {

    override fun debug(message: String) = KLog.d { message }

    override fun log(message: String) = KLog.i { message }

    override fun logState(state: UIState) = KLog.i { "[STATE] - $state" }

    override fun logEvent(event: UIEvent) = KLog.i { "<EVENT> - $event" }

    override fun logError(errorMessage: String, error: Exception?) {
        KLog.e(error) { "<EVENT> - !ERROR! - $errorMessage" }
    }
}
