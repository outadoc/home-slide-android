package fr.outadoc.homeslide.app.onboarding.rest

import fr.outadoc.homeslide.hassapi.api.DiscoveryApi
import fr.outadoc.homeslide.hassapi.model.discovery.ApiStatus
import fr.outadoc.homeslide.hassapi.model.discovery.DiscoveryInfo
import fr.outadoc.homeslide.hassapi.repository.DiscoveryRepository
import fr.outadoc.homeslide.rest.util.wrapResponse

class DiscoveryRepositoryImpl(private val client: DiscoveryApi) :
    DiscoveryRepository {

    override suspend fun getDiscoveryInfo(baseUrl: String): Result<DiscoveryInfo> =
        wrapResponse {
            client.getDiscoveryInfo(
                baseUrl
            )
        }

    override suspend fun getApiStatus(baseUrl: String, token: String): Result<ApiStatus> =
        wrapResponse {
            client.getApiStatus(
                baseUrl,
                "Bearer $token"
            )
        }
}