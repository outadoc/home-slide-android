package fr.outadoc.homeslide.zeroconf

import android.net.nsd.NsdServiceInfo

interface ZeroconfDiscoveryService {

    fun setOnServiceDiscoveredListener(onServiceDiscovered: ((NsdServiceInfo) -> Unit)?)

    fun startDiscovery()

    fun stopDiscovery()
}
