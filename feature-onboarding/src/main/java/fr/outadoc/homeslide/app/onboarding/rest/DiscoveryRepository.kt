package fr.outadoc.homeslide.app.onboarding.rest

import fr.outadoc.homeslide.app.onboarding.model.ApiStatus
import fr.outadoc.homeslide.app.onboarding.model.DiscoveryInfo

interface DiscoveryRepository {

    suspend fun getDiscoveryInfo(baseUrl: String): Result<DiscoveryInfo>
    suspend fun getApiStatus(baseUrl: String, token: String): Result<ApiStatus>
}