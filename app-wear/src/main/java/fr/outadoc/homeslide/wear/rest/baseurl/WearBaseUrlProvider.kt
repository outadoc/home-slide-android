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

package fr.outadoc.homeslide.wear.rest.baseurl

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.rest.NetworkAccessManager
import fr.outadoc.homeslide.rest.baseurl.BaseUrlConfigProvider
import fr.outadoc.homeslide.rest.baseurl.BaseUrlProvider
import fr.outadoc.homeslide.rest.baseurl.BaseUrlRank
import fr.outadoc.homeslide.rest.util.toUrlOrNull
import kotlin.properties.Delegates
import okhttp3.HttpUrl

/**
 * Base URL provider for Wear.
 * Will bind to the available Wi-Fi network if trying to reach the local base URL.
 */
class WearBaseUrlProvider(
    private val config: BaseUrlConfigProvider,
    private val connectivityManager: ConnectivityManager
) : BaseUrlProvider, NetworkAccessManager {

    private val localBaseUri: HttpUrl?
        get() {
            connectivityManager.bindProcessToNetwork(currentWifiNetwork)
            return config.localInstanceBaseUrl.toUrlOrNull()
        }

    private val remoteBaseUri: HttpUrl?
        get() {
            connectivityManager.bindProcessToNetwork(null)
            return config.remoteInstanceBaseUrl.toUrlOrNull()
        }

    private var preferLocalBaseUrl: Boolean = false

    private var currentWifiNetwork: Network?
        by Delegates.observable(null) { _, _, value ->
            preferLocalBaseUrl = value != null
        }

    init {
        val wifiRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        connectivityManager.registerNetworkCallback(
            wifiRequest,
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    KLog.d { "Connected to Wi-Fi" }
                    currentWifiNetwork = network
                }

                override fun onLost(network: Network) {
                    KLog.d { "Disconnected from Wi-Fi, preferring remote base URL" }
                    currentWifiNetwork = null
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

    override fun releaseNetwork() {
        connectivityManager.bindProcessToNetwork(null)
    }
}
