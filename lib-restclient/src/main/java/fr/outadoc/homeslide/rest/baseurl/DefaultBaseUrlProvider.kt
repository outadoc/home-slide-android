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

package fr.outadoc.homeslide.rest.baseurl

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.rest.NetworkAccessManager
import fr.outadoc.homeslide.rest.util.toUrlOrNull
import okhttp3.HttpUrl

/**
 * Default base URL provider.
 *
 * Will provide as a primary URL the one that was last successfully used.
 * When connecting to Wi-Fi, favor the local URL initially. When disconnecting, favor the remote.
 */
class DefaultBaseUrlProvider(
    private val config: BaseUrlConfigProvider,
    connectivityManager: ConnectivityManager
) : BaseUrlProvider, NetworkAccessManager {

    private val localBaseUri: HttpUrl?
        get() = config.localInstanceBaseUrl.toUrlOrNull()

    private val remoteBaseUri: HttpUrl?
        get() = config.remoteInstanceBaseUrl.toUrlOrNull()

    private var preferLocalBaseUrl = true

    init {
        val localNetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()

        connectivityManager.registerNetworkCallback(
            localNetworkRequest,
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    KLog.d { "Connected to Wi-Fi, preferring local base URL" }
                    preferLocalBaseUrl = true
                }

                override fun onLost(network: Network) {
                    KLog.d { "Disconnected from Wi-Fi, preferring remote base URL" }
                    preferLocalBaseUrl = false
                }
            })
    }

    override fun getBaseUrl(rank: BaseUrlRank) =
        when (rank) {
            BaseUrlRank.PRIMARY -> if (preferLocalBaseUrl) localBaseUri else remoteBaseUri
            BaseUrlRank.SECONDARY -> if (preferLocalBaseUrl) remoteBaseUri else localBaseUri
        }

    override fun rememberSuccessWith(which: BaseUrlRank?) {
        if (which == BaseUrlRank.SECONDARY) {
            // Make the secondary URL the primary one
            preferLocalBaseUrl = !preferLocalBaseUrl

            KLog.d { "$which base URL succeeded, flipping preferLocalBaseUrl to $preferLocalBaseUrl" }
        }
    }

    override fun releaseNetwork() {}
}
