package fr.outadoc.homeslide.common.log

import fr.outadoc.homeslide.logging.KLog
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE

class KoinCustomLogger : Logger() {

    override fun log(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> KLog.d { msg }
            Level.INFO -> KLog.i { msg }
            Level.ERROR -> KLog.e { msg }
        }
    }
}