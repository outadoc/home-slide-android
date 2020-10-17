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

import fr.outadoc.homeslide.hassapi.model.discovery.ApiStatus
import fr.outadoc.homeslide.hassapi.model.discovery.DiscoveryInfo
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
