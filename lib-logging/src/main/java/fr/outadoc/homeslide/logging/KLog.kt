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

package fr.outadoc.homeslide.logging

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

@Suppress("NOTHING_TO_INLINE", "unused")
object KLog {

    @PublishedApi
    internal inline fun log(t: Throwable? = null, content: () -> Unit) {
        if (Timber.treeCount() > 0) content()
        if (t != null) FirebaseCrashlytics.getInstance().recordException(t)
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
