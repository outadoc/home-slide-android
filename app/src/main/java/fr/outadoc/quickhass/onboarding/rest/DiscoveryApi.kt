package fr.outadoc.quickhass.onboarding.rest

import fr.outadoc.quickhass.model.DiscoveryInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DiscoveryApi {

    @GET("{baseUrl}/api/discovery_info")
    suspend fun getDiscoveryInfo(@Path("baseUrl") baseUrl: String): Response<DiscoveryInfo>
}