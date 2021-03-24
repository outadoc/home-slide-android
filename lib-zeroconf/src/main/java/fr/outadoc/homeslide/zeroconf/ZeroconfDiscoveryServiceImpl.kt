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
            nsdManager.resolveService(
                service,
                object : NsdManager.ResolveListener {
                    override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) = Unit
                    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                        onServiceDiscovered?.let { it(parseServiceInfo(serviceInfo)) }
                    }
                }
            )
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
