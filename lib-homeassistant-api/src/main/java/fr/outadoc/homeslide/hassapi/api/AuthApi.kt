/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
