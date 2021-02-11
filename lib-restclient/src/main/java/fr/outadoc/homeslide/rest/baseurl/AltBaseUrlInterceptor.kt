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
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.jvm.Throws

class AltBaseUrlInterceptor(private val config: BaseUrlProvider) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.tryWithPossibleBaseUrls()
    }

    private sealed class AttemptResult {
        class Success(val response: Response) : AttemptResult()
        sealed class Error : AttemptResult() {
            class WithResponse(val response: Response) : Error()
            class Exception(val e: Throwable) : Error()
        }
    }

    @Throws(IOException::class)
    private fun Interceptor.Chain.tryWithPossibleBaseUrls(): Response {
        var lastError: AttemptResult.Error? = null

        BaseUrlRank.values()
            .forEach { rank ->
                when (val res = tryWithBaseUrl(rank)) {
                    is AttemptResult.Success -> return res.response
                    is AttemptResult.Error -> lastError = res
                }
            }

        when (val error = lastError!!) {
            is AttemptResult.Error.Exception -> throw error.e
            is AttemptResult.Error.WithResponse -> return error.response
        }
    }

    private fun Interceptor.Chain.tryWithBaseUrl(rank: BaseUrlRank): AttemptResult {
        val originalRequestUrl = request().url()

        KLog.d { "Trying URL for rank $rank" }

        val targetBaseUrl = checkNotNull(config.getBaseUrl(rank)) {
            "No such URL for rank $rank"
        }

        val targetUrl = originalRequestUrl.substituteHost(targetBaseUrl)

        KLog.d { "Transformed URL: $targetUrl" }

        val req = request()
            .newBuilder()
            .url(targetUrl)
            .build()

        return proceed(req).also { res ->
            config.rememberSuccessWith(if (res.isSuccessful) rank else null)
        }.let { res ->
            if (res.isSuccessful) AttemptResult.Success(res)
            else AttemptResult.Error.WithResponse(res)
        }
    }

    private fun HttpUrl.substituteHost(baseUrl: HttpUrl): HttpUrl {
        return baseUrl.newBuilder()
            .addEncodedPathSegments(encodedPath().trimStart('/'))
            .build()
    }
}
