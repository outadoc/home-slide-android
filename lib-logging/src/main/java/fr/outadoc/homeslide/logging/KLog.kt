package fr.outadoc.homeslide.logging

import timber.log.Timber

@Suppress("NOTHING_TO_INLINE", "unused")
object KLog {

    @PublishedApi
    internal inline fun log(content: () -> Unit) {
        if (Timber.treeCount() > 0) content()
    }

    inline fun v(t: Throwable? = null, message: () -> String) = log { Timber.v(t, message()) }
    inline fun v(t: Throwable?) = Timber.v(t)

    inline fun d(t: Throwable? = null, message: () -> String) = log { Timber.d(t, message()) }
    inline fun d(t: Throwable?) = Timber.d(t)

    inline fun i(t: Throwable? = null, message: () -> String) = log { Timber.i(t, message()) }
    inline fun i(t: Throwable?) = Timber.i(t)

    inline fun w(t: Throwable? = null, message: () -> String) = log { Timber.w(t, message()) }
    inline fun w(t: Throwable?) = Timber.w(t)

    inline fun e(t: Throwable? = null, message: () -> String) = log { Timber.e(t, message()) }
    inline fun e(t: Throwable?) = Timber.e(t)

    fun enableDebugLogging() {
        Timber.plant(Timber.DebugTree())
    }
}
