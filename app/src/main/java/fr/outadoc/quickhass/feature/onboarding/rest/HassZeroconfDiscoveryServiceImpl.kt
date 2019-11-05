package fr.outadoc.quickhass.feature.onboarding.rest

import android.net.nsd.NsdManager

class HassZeroconfDiscoveryServiceImpl(nsdManager: NsdManager) : ZeroconfDiscoveryServiceImpl(nsdManager, SERVICE_TYPE) {

    companion object {
        const val SERVICE_TYPE = "_home-assistant._tcp."
    }
}