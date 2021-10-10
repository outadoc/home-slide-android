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

package fr.outadoc.homeslide.app.onboarding.feature.host.rest

import fr.outadoc.homeslide.hassapi.api.DiscoveryApi
import fr.outadoc.homeslide.hassapi.repository.DiscoveryRepository

class DiscoveryRepositoryImpl(private val client: DiscoveryApi) : DiscoveryRepository {

    override suspend fun isInstanceReachable(baseUrl: String): Boolean {
        // If we get a 200 OK response, we're good.
        // If we get a 401 Unauthorized, it's expected - we haven't sent a token, assume we're good.
        val validResponseCodes = listOf(200, 401)
        val responseCode = client.getApiStatus(baseUrl, token = null).code()
        return responseCode in validResponseCodes
    }
}
