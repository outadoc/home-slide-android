package fr.outadoc.quickhass.feature.slideover.rest

import fr.outadoc.quickhass.feature.slideover.model.Service
import fr.outadoc.quickhass.feature.slideover.model.State
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HomeAssistantApi {

    @GET("/api/states")
    suspend fun getStates(): Response<List<State>>

    @GET("/api/services")
    suspend fun getServices(): Response<List<Service>>

    @POST("/api/services/{domain}/{service}")
    suspend fun callService(
        @Path("domain") domain: String,
        @Path("service") service: String,
        @Body params: Map<String, String>
    ): Response<List<State>>
}