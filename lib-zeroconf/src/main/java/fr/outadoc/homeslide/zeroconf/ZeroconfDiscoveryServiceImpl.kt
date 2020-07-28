package fr.outadoc.homeslide.zeroconf

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo

abstract class ZeroconfDiscoveryServiceImpl<T>(
    private val nsdManager: NsdManager,
    private val serviceType: String
) : ZeroconfDiscoveryService<T>, NsdManager.DiscoveryListener {

    enum class State { IDLE, DISCOVERING }

    private var onServiceDiscovered: ((T) -> Unit)? = null

    private var state: State =
        State.IDLE

    override fun setOnServiceDiscoveredListener(onServiceDiscovered: ((T) -> Unit)?) {
        this.onServiceDiscovered = onServiceDiscovered
    }

    override fun startDiscovery() {
        if (state == State.IDLE) {
            nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, this)
        }
    }

    override fun stopDiscovery() {
        if (state == State.DISCOVERING) {
            nsdManager.stopServiceDiscovery(this)
        }
    }

    override fun onDiscoveryStarted(serviceType: String?) {
        state = State.DISCOVERING
    }

    override fun onDiscoveryStopped(serviceType: String?) {
        state = State.IDLE
    }

    override fun onServiceLost(serviceInfo: NsdServiceInfo?) = Unit

    override fun onServiceFound(service: NsdServiceInfo) {
        if (service.serviceType == serviceType) {
            nsdManager.resolveService(service, object : NsdManager.ResolveListener {
                override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) = Unit
                override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                    onServiceDiscovered?.let { it(parseServiceInfo(serviceInfo)) }
                }
            })
        }
    }

    abstract fun parseServiceInfo(serviceInfo: NsdServiceInfo): T

    override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
        state = State.IDLE
        nsdManager.stopServiceDiscovery(this)
    }

    override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
        state = State.IDLE
        nsdManager.stopServiceDiscovery(this)
    }
}
