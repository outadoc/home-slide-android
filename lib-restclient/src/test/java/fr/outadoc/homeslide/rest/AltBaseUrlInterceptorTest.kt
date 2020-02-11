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