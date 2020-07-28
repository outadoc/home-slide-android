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
                hostName = "${host.hostAddress}:$port",
                baseUrl = attributes["base_url"]?.decodeToString(),
                version = attributes["version"]?.decodeToString(),
                instanceName = serviceName
            )
        }
    }
}
