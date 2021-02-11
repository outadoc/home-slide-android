/*
 * Copyright 2021 Baptiste Candellier
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

package fr.outadoc.homeslide.rest.baseurl

import fr.outadoc.homeslide.rest.util.toUrlOrNull
import okhttp3.HttpUrl

/**
 * Default base URL provider.
 *
 * Will provide as a primary URL the one that was last successfully used.
 * Initially, favor the local base URL.
 */
class DefaultBaseUrlProvider(private val config: BaseUrlConfigProvider) : BaseUrlProvider {

    // TODO Observe network state.
    // Clear preferred url on network change,
    // When WiFi is available prefer local by default

    private val localBaseUri: HttpUrl?
        get() = config.localInstanceBaseUrl.toUrlOrNull()

    private val remoteBaseUri: HttpUrl?
        get() = config.remoteInstanceBaseUrl.toUrlOrNull()

    private val preferLocalBaseUrlByDefault = true

    private var preferLocalBaseUrl = preferLocalBaseUrlByDefault

    override fun getBaseUrl(rank: BaseUrlRank) =
        when (rank) {
            BaseUrlRank.PRIMARY -> if (preferLocalBaseUrl) localBaseUri else remoteBaseUri
            BaseUrlRank.SECONDARY -> if (preferLocalBaseUrl) remoteBaseUri else localBaseUri
        }

    override fun rememberSuccessWith(which: BaseUrlRank?) {
        preferLocalBaseUrl = if (which == BaseUrlRank.SECONDARY) {
            // Make the secondary URL the primary one
            !preferLocalBaseUrl
        } else {
            preferLocalBaseUrlByDefault
        }
    }
}