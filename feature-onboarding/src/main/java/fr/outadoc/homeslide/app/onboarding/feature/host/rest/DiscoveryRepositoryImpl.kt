package fr.outadoc.homeslide.app.onboarding.feature.host.rest

import fr.outadoc.homeslide.hassapi.api.DiscoveryApi
import fr.outadoc.homeslide.hassapi.model.discovery.ApiStatus
import fr.outadoc.homeslide.hassapi.model.discovery.DiscoveryInfo
import fr.outadoc.homeslide.hassapi.repository.DiscoveryRepository
import fr.outadoc.homeslide.rest.util.getResponseOrThrow

class DiscoveryRepositoryImpl(private val client: DiscoveryApi) :
    DiscoveryRepository {

    override suspend fun getDiscoveryInfo(baseUrl: String): DiscoveryInfo =
        client.getDiscoveryInfo(baseUrl).getResponseOrThrow()

    override suspend fun getApiStatus(baseUrl: String, token: String): ApiStatus =
        client.getApiStatus(baseUrl, "Bearer $token").getResponseOrThrow()
}
