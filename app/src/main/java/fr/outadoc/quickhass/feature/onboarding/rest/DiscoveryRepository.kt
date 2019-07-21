package fr.outadoc.quickhass.feature.onboarding.rest

import fr.outadoc.quickhass.feature.onboarding.model.ApiStatus
import fr.outadoc.quickhass.feature.onboarding.model.DiscoveryInfo

interface DiscoveryRepository {

    suspend fun getDiscoveryInfo(baseUrl: String): Result<DiscoveryInfo>
    suspend fun getApiStatus(baseUrl: String, token: String): Result<ApiStatus>
}