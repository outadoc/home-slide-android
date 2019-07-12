package fr.outadoc.quickhass.onboarding.rest

import fr.outadoc.quickhass.model.DiscoveryInfo
import fr.outadoc.quickhass.rest.wrapResponse

class DiscoveryRepositoryImpl : DiscoveryRepository {

    private val client =
        SimpleRestClient.create<DiscoveryApi>()

    override suspend fun getDiscoveryInfo(baseUrl: String): Result<DiscoveryInfo> =
        wrapResponse { client.getDiscoveryInfo(baseUrl) }
}