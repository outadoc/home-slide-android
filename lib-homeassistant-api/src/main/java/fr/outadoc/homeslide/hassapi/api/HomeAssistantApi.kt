package fr.outadoc.homeslide.hassapi.api

import fr.outadoc.homeslide.hassapi.model.EntityState
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HomeAssistantApi {

    @GET("/api/states")
    suspend fun getStates(): Response<List<EntityState>>

    @POST("/api/services/{domain}/{service}")
    suspend fun callService(
        @Path("domain") domain: String,
        @Path("service") service: String,
        @Body params: Map<String, String>
    ): Response<List<EntityState>>
}
