package fr.outadoc.quickhass.rest

import com.github.ajalt.timberkt.Timber
import fr.outadoc.quickhass.feature.slideover.rest.RestClient
import fr.outadoc.quickhass.feature.slideover.rest.toUrl
import fr.outadoc.quickhass.feature.slideover.rest.toUrlOrNull
import fr.outadoc.quickhass.preferences.PreferenceRepository
import fr.outadoc.quickhass.preferences.PreferredBaseUrl
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class AltBaseUrlInterceptor(private val prefs: PreferenceRepository) : Interceptor {

    private val baseUri: HttpUrl?
        get() = prefs.instanceBaseUrl.toUrlOrNull()

    private val altBaseUri: HttpUrl?
        get() = prefs.altInstanceBaseUrl.toUrlOrNull()

    private val preferredBaseUrl: PreferredBaseUrl
        get() = prefs.preferredBaseUrl

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
                    val res = chain.proceed(req)
                    if (res.isSuccessful) {
                        prefs.preferredBaseUrl = type
                        return res
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
            return requestUrl.toString().replace(RestClient.PLACEHOLDER_BASE_URL, newBase.toString()).toUrl()
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
