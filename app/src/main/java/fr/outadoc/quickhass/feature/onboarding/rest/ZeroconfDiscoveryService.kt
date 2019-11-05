package fr.outadoc.quickhass.feature.onboarding.rest

import android.net.nsd.NsdServiceInfo

interface ZeroconfDiscoveryService {

    fun setOnServiceDiscoveredListener(onServiceDiscovered: ((NsdServiceInfo) -> Unit)?)

    fun startDiscovery()

    fun stopDiscovery()
}
