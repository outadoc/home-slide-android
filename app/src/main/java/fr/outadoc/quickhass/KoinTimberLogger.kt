package fr.outadoc.quickhass

import com.github.ajalt.timberkt.Timber.d
import com.github.ajalt.timberkt.Timber.e
import com.github.ajalt.timberkt.Timber.i
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE

class KoinTimberLogger : Logger() {

    override fun log(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> d { msg }
            Level.INFO -> i { msg }
            Level.ERROR -> e { msg }
        }
    }

}