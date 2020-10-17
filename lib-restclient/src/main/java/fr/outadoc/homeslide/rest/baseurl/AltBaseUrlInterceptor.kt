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

package fr.outadoc.homeslide.rest.baseurl

import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.rest.util.PLACEHOLDER_BASE_URL
import fr.outadoc.homeslide.rest.util.toUrl
import fr.outadoc.homeslide.rest.util.toUrlOrNull
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class AltBaseUrlInterceptor(private val config: BaseUrlConfigProvider) : Interceptor {

    private val baseUri: HttpUrl?
        get() = config.instanceBaseUrl.toUrlOrNull()

    private val altBaseUri: HttpUrl?
        get() = config.altInstanceBaseUrl.toUrlOrNull()

    private val preferredBaseUrl: PreferredBaseUrl
        get() = config.preferredBaseUrl

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()

        getUrlsToTry(req.url())
            .map { (type, url) ->
                type to chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
            }.forEach { (type, req) ->
                try {
                    return chain.proceed(req).also { res ->
                        if (res.isSuccessful) {
                            config.preferredBaseUrl = type
                        }
                    }
                } catch (e: Exception) {
                    KLog.e(e)
                }
            }

        return chain.proceed(req)
    }

    fun getUrlsToTry(requestUrl: HttpUrl): List<Pair<PreferredBaseUrl, HttpUrl>> {
        fun replaceBaseUrl(newBase: HttpUrl?): HttpUrl? {
            if (newBase == null) return null
            return requestUrl.toString().replace(PLACEHOLDER_BASE_URL, newBase.toString()).toUrl()
        }

        val internalUrl = PreferredBaseUrl.PRIMARY to replaceBaseUrl(baseUri)
        val externalUrl = PreferredBaseUrl.ALTERNATIVE to replaceBaseUrl(altBaseUri)

        return when (preferredBaseUrl) {
            PreferredBaseUrl.PRIMARY -> listOf(internalUrl, externalUrl)
            PreferredBaseUrl.ALTERNATIVE -> listOf(externalUrl, internalUrl)
        }.mapNotNull { (type, url) ->
            if (url != null) type to url else null
        }
    }
}
