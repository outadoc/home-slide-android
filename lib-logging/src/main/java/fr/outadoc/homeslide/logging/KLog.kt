package fr.outadoc.homeslide.logging

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

@Suppress("NOTHING_TO_INLINE", "unused")
object KLog {

    @PublishedApi
    internal inline fun log(t: Throwable? = null, content: () -> Unit) {
        if (t != null) FirebaseCrashlytics.getInstance().recordException(t)
        if (Timber.treeCount() > 0) content()
    }

    inline fun v(t: Throwable?) =
        log(t) { Timber.v(t) }

    inline fun v(t: Throwable? = null, message: () -> String) =
        log(t) { Timber.v(t, message()) }

    inline fun d(t: Throwable?) =
        log(t) { Timber.d(t) }

    inline fun d(t: Throwable? = null, message: () -> String) =
        log(t) { Timber.d(t, message()) }

    inline fun i(t: Throwable?) =
        log(t) { Timber.i(t) }

    inline fun i(t: Throwable? = null, message: () -> String) =
        log(t) { Timber.i(t, message()) }

    inline fun w(t: Throwable?) =
        log(t) { Timber.w(t) }

    inline fun w(t: Throwable? = null, message: () -> String) =
        log(t) { Timber.w(t, message()) }

    inline fun e(t: Throwable?) =
        log(t) { Timber.e(t) }

    inline fun e(t: Throwable? = null, message: () -> String) =
        log(t) { Timber.e(t, message()) }

    fun enableDebugLogging() {
        Timber.plant(Timber.DebugTree())
    }
}
