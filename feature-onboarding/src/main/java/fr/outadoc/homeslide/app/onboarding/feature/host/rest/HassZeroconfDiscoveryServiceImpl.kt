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

package fr.outadoc.homeslide.app.onboarding.feature.host.rest

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import fr.outadoc.homeslide.app.onboarding.feature.host.model.ZeroconfHost
import fr.outadoc.homeslide.zeroconf.ZeroconfDiscoveryServiceImpl

class HassZeroconfDiscoveryServiceImpl(nsdManager: NsdManager) :
    ZeroconfDiscoveryServiceImpl<ZeroconfHost>(nsdManager, SERVICE_TYPE) {

    companion object {
        const val SERVICE_TYPE = "_home-assistant._tcp."
    }

    override fun parseServiceInfo(serviceInfo: NsdServiceInfo): ZeroconfHost {
        return with(serviceInfo) {
            ZeroconfHost(
                localBaseUrl = tryGetAttribute("internal_url") ?: "http://${host.hostAddress}:$port",
                remoteBaseUrl = tryGetAttribute("external_url") ?: tryGetAttribute("base_url"),
                version = tryGetAttribute("version"),
                instanceName = tryGetAttribute("location_name"),
                uuid = tryGetAttribute("uuid") ?: host.hostAddress
            )
        }
    }
}
