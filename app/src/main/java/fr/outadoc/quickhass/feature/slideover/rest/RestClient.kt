package fr.outadoc.quickhass.feature.slideover.rest

import fr.outadoc.quickhass.preferences.PreferenceRepository
import fr.outadoc.quickhass.preferences.PreferredBaseUrl
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class RestClient<T>(private val type: Class<T>, private val prefs: PreferenceRepository) {

    private val baseUri: HttpUrl?
        get() = prefs.instanceBaseUrl.toUrlOrNull()

    private val altBaseUri: HttpUrl?
        get() = prefs.altInstanceBaseUrl.toUrlOrNull()

    private val preferredBaseUrl: PreferredBaseUrl
        get() = prefs.preferredBaseUrl

    private fun getUrlsToTry(requestUrl: HttpUrl): List<Pair<PreferredBaseUrl, HttpUrl>> {
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

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val newHeaders = chain.request()
            .headers()
            .newBuilder()
            .set("Authorization", "Bearer ${prefs.accessToken}")
            .build()

        val newRequest = chain.request()
            .newBuilder()
            .headers(newHeaders)
            .build()

        chain.proceed(newRequest)
    }

    private val altBaseUrlInterceptor = Interceptor { chain ->
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
                        return@Interceptor res
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        chain.proceed(req)
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .addInterceptor(altBaseUrlInterceptor)
        .build()

    private val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(PLACEHOLDER_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    val api: T
        get() = retrofit.create(type)

    companion object {
        const val PLACEHOLDER_BASE_URL = "https://example.com/"
        const val CONNECT_TIMEOUT_SECONDS = 3L

        inline fun <reified T> create(prefs: PreferenceRepository): T =
            RestClient(T::class.java, prefs).api
    }
}