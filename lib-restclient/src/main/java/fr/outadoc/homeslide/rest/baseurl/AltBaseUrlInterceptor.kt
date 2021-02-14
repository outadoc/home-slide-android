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
import fr.outadoc.homeslide.rest.throwable.CompositeIOException
import java.io.IOException
import kotlin.jvm.Throws
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class AltBaseUrlInterceptor(private val config: BaseUrlProvider) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.tryWithPossibleBaseUrls()
    }

    private sealed class AttemptResult {
        class Success(val response: Response) : AttemptResult()
        class Error(val e: Throwable) : AttemptResult()
        object Ignored : AttemptResult()
    }

    @Throws(IOException::class)
    private fun Interceptor.Chain.tryWithPossibleBaseUrls(): Response {
        val errorList = mutableListOf<Throwable>()

        BaseUrlRank.values()
            .forEach { rank ->
                when (val res = tryWithBaseUrl(rank)) {
                    is AttemptResult.Success -> return res.response
                    is AttemptResult.Error -> errorList.add(res.e)
                }
            }

        if (errorList.isNotEmpty()) {
            throw CompositeIOException(errorList.toList())
        }

        throw IOException("No base URL configured. Please check your settings.")
    }

    private fun Interceptor.Chain.tryWithBaseUrl(rank: BaseUrlRank): AttemptResult {
        val originalRequestUrl = request().url()

        KLog.d { "Trying URL for rank $rank" }

        val targetBaseUrl = config.getBaseUrl(rank) ?: return AttemptResult.Ignored
        val targetUrl = originalRequestUrl.substituteHost(targetBaseUrl)

        KLog.d { "Transformed URL: $targetUrl" }

        val req = request()
            .newBuilder()
            .url(targetUrl)
            .build()

        return try {
            proceed(req).let { response ->
                if (response.isSuccessful) AttemptResult.Success(response)
                else AttemptResult.Error(IOException("HTTP Error: ${response.code()}"))
            }
        } catch (e: IOException) {
            AttemptResult.Error(e)
        }.also { result ->
            config.rememberSuccessWith(if (result is AttemptResult.Success) rank else null)
        }
    }

    private fun HttpUrl.substituteHost(baseUrl: HttpUrl): HttpUrl {
        return baseUrl.newBuilder()
            .addEncodedPathSegments(encodedPath().trimStart('/'))
            .build()
    }
}
