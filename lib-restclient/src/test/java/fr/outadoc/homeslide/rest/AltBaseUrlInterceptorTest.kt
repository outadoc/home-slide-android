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

package fr.outadoc.homeslide.rest

import fr.outadoc.homeslide.rest.baseurl.AltBaseUrlInterceptor
import fr.outadoc.homeslide.rest.baseurl.BaseUrlConfigProvider
import fr.outadoc.homeslide.rest.baseurl.PreferredBaseUrl
import fr.outadoc.homeslide.rest.baseurl.PreferredBaseUrl.ALTERNATIVE
import fr.outadoc.homeslide.rest.baseurl.PreferredBaseUrl.PRIMARY
import fr.outadoc.homeslide.rest.util.PLACEHOLDER_BASE_URL
import fr.outadoc.homeslide.rest.util.toUrl
import okhttp3.HttpUrl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class AltBaseUrlInterceptorTest {

    @Test
    fun `Given primary URL is preferred, then list contains primary URL and alternative URL`() {
        setupWith(
            baseUrl = "https://test.xyz",
            altBaseUrl = "https://test.abc",
            preferred = PRIMARY
        ).apply {
            assertEquals(
                listOf(
                    PRIMARY to "https://test.xyz/test/".toUrl(),
                    ALTERNATIVE to "https://test.abc/test/".toUrl()
                ),
                getUrlsToTry(getUrlForPath("test/"))
            )
        }
    }

    @Test
    fun `Given alternative URL is preferred, then list contains alternative URL and primary URL`() {
        setupWith(
            baseUrl = "https://test.xyz",
            altBaseUrl = "https://test.abc",
            preferred = ALTERNATIVE
        ).apply {
            assertEquals(
                listOf(
                    ALTERNATIVE to "https://test.abc/test/".toUrl(),
                    PRIMARY to "https://test.xyz/test/".toUrl()
                ),
                getUrlsToTry(getUrlForPath("test/"))
            )
        }
    }

    @Test
    fun `Given primary URL is preferred, and no alternative URL is provided, then list contains primary URL`() {
        setupWith(
            baseUrl = "https://test.xyz",
            altBaseUrl = null,
            preferred = PRIMARY
        ).apply {
            assertEquals(
                listOf(
                    PRIMARY to "https://test.xyz/test/".toUrl()
                ),
                getUrlsToTry(getUrlForPath("test/"))
            )
        }
    }

    @Test
    fun `Given alternative URL is preferred, and no alternative URL is provided, then list contains primary URL`() {
        setupWith(
            baseUrl = "https://test.xyz",
            altBaseUrl = null,
            preferred = ALTERNATIVE
        ).apply {
            assertEquals(
                listOf(
                    PRIMARY to "https://test.xyz/test/".toUrl()
                ),
                getUrlsToTry(getUrlForPath("test/"))
            )
        }
    }

    private fun getUrlForPath(path: String): HttpUrl {
        return (PLACEHOLDER_BASE_URL + path).toUrl()
    }

    private fun setupWith(
        baseUrl: String,
        altBaseUrl: String?,
        preferred: PreferredBaseUrl
    ): AltBaseUrlInterceptor {
        val repo = Mockito.mock(BaseUrlConfigProvider::class.java).apply {
            `when`(instanceBaseUrl).thenReturn(baseUrl)
            `when`(altInstanceBaseUrl).thenReturn(altBaseUrl)
            `when`(preferredBaseUrl).thenReturn(preferred)
        }

        return AltBaseUrlInterceptor(repo)
    }
}
