package fr.outadoc.quickhass.onboarding.rest

import fr.outadoc.quickhass.model.DiscoveryInfo

interface DiscoveryRepository {

    suspend fun getDiscoveryInfo(baseUrl: String): Result<DiscoveryInfo>
}