package fr.outadoc.homeslide.app.onboarding.rest

import fr.outadoc.homeslide.app.onboarding.model.ApiStatus
import fr.outadoc.homeslide.app.onboarding.model.DiscoveryInfo
import fr.outadoc.homeslide.shared.rest.wrapResponse

class DiscoveryRepositoryImpl(private val client: DiscoveryApi) : DiscoveryRepository {

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