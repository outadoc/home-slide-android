package fr.outadoc.homeslide.hassapi.repository

import fr.outadoc.homeslide.hassapi.model.discovery.ApiStatus
import fr.outadoc.homeslide.hassapi.model.discovery.DiscoveryInfo

interface DiscoveryRepository {

    suspend fun getDiscoveryInfo(baseUrl: String): DiscoveryInfo
    suspend fun getApiStatus(baseUrl: String, token: String): ApiStatus
}
