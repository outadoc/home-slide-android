package fr.outadoc.homeslide.rest.baseurl

import com.github.ajalt.timberkt.Timber
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
                    Timber.e(e)
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
