package fr.outadoc.homeslide.hassapi.api

import fr.outadoc.homeslide.hassapi.model.auth.Token
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi {

    @FormUrlEncoded
    @POST("/auth/token")
    suspend fun getToken(
        @Field("code") code: String,
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String = "authorization_code"
    ): Response<Token>

    @FormUrlEncoded
    @POST("/auth/token")
    suspend fun refreshToken(
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): Response<Token>

    @FormUrlEncoded
    @POST("/auth/token")
    suspend fun revokeToken(
        @Field("token") refreshToken: String,
        @Field("action") action: String = "revoke"
    )
}
