package fr.outadoc.quickhass.feature.onboarding.rest

import fr.outadoc.quickhass.feature.onboarding.model.DiscoveryInfo

interface DiscoveryRepository {

    suspend fun getDiscoveryInfo(baseUrl: String): Result<DiscoveryInfo>
}