/*
 * Copyright 2021 Baptiste Candellier
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

package fr.outadoc.homeslide.rest

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume

suspend fun ConnectivityManager.requestNetwork(request: NetworkRequest, timeoutMs: Int): Network? =
    withTimeout(timeoutMs.toLong()) {
        suspendCancellableCoroutine { cont ->
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    cont.resume(network)
                }

                override fun onUnavailable() {
                    cont.resume(null)
                }

                override fun onLost(network: Network) {
                    cont.resume(null)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestNetwork(request, callback, timeoutMs)
            } else {
                requestNetwork(request, callback)
            }

            cont.invokeOnCancellation {
                unregisterNetworkCallback(callback)
            }
        }
    }