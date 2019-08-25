package fr.outadoc.quickhass.feature.onboarding.rest

import fr.outadoc.quickhass.feature.onboarding.model.ApiStatus
import fr.outadoc.quickhass.feature.onboarding.model.DiscoveryInfo
import fr.outadoc.quickhass.feature.slideover.rest.wrapResponse

class DiscoveryRepositoryImpl : DiscoveryRepository {

    private val client: DiscoveryApi by lazy {
        SimpleRestClient.create<DiscoveryApi>()
    }

    override suspend fun getDiscoveryInfo(baseUrl: String): Result<DiscoveryInfo> =
        wrapResponse { client.getDiscoveryInfo(baseUrl) }

    override suspend fun getApiStatus(baseUrl: String, token: String): Result<ApiStatus> =
        wrapResponse { client.getApiStatus(baseUrl, "Bearer $token") }
}