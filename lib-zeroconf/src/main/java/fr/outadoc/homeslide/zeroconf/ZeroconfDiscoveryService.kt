package fr.outadoc.homeslide.zeroconf

interface ZeroconfDiscoveryService<T> {

    fun setOnServiceDiscoveredListener(onServiceDiscovered: ((T) -> Unit)?)

    fun startDiscovery()

    fun stopDiscovery()
}
