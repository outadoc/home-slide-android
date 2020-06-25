package fr.outadoc.homeslide.common.log

import fr.outadoc.homeslide.logging.KLog
import okhttp3.logging.HttpLoggingInterceptor

class OkHttpCustomLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) = KLog.d { message }
}