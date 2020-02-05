package fr.outadoc.homeslide.app.onboarding.rest

import fr.outadoc.homeslide.app.onboarding.model.ApiStatus
import fr.outadoc.homeslide.app.onboarding.model.DiscoveryInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface DiscoveryApi {

    @GET("{baseUrl}/api/discovery_info")
    suspend fun getDiscoveryInfo(
        @Path("baseUrl", encoded = true) baseUrl: String
    ): Response<DiscoveryInfo>

    @GET("{baseUrl}/api/")
    suspend fun getApiStatus(
        @Path("baseUrl", encoded = true) baseUrl: String,
        @Header("Authorization") token: String
    ): Response<ApiStatus>
}