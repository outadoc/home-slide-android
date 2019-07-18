package fr.outadoc.quickhass.feature.onboarding.rest

import fr.outadoc.quickhass.feature.onboarding.model.DiscoveryInfo
import fr.outadoc.quickhass.feature.slideover.rest.wrapResponse

class DiscoveryRepositoryImpl : DiscoveryRepository {

    private val client =
        SimpleRestClient.create<DiscoveryApi>()

    override suspend fun getDiscoveryInfo(baseUrl: String): Result<DiscoveryInfo> =
        wrapResponse { client.getDiscoveryInfo(baseUrl) }
}