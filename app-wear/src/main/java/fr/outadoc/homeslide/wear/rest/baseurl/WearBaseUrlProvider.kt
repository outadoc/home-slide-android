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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.rest.baseurl.BaseUrlConfigProvider
import fr.outadoc.homeslide.rest.baseurl.BaseUrlProvider
import fr.outadoc.homeslide.rest.baseurl.BaseUrlRank
import fr.outadoc.homeslide.rest.util.toUrlOrNull
import fr.outadoc.homeslide.wear.MainActivity
import okhttp3.HttpUrl

class WearBaseUrlProvider(
    private val context: Context,
    private val config: BaseUrlConfigProvider,
    private val connectivityManager: ConnectivityManager
) : BaseUrlProvider {

    private val localBaseUri: HttpUrl?
        get() {
            requestWifiNetwork()
            return config.localInstanceBaseUrl.toUrlOrNull()
        }

    private val remoteBaseUri: HttpUrl?
        get() = config.remoteInstanceBaseUrl.toUrlOrNull()

    private var preferLocalBaseUrl = false

    private val wifiRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()

    init {
        connectivityManager.registerNetworkCallback(
            wifiRequest,
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

    private fun requestWifiNetwork() {
        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        connectivityManager.requestNetwork(wifiRequest, pi)
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
}