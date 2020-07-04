package fr.outadoc.homeslide.app.onboarding.feature.host.rest

import android.net.nsd.NsdManager
import fr.outadoc.homeslide.zeroconf.ZeroconfDiscoveryServiceImpl

class HassZeroconfDiscoveryServiceImpl(nsdManager: NsdManager) : ZeroconfDiscoveryServiceImpl(nsdManager, SERVICE_TYPE) {

    companion object {
        const val SERVICE_TYPE = "_home-assistant._tcp."
    }
}
