package fr.outadoc.quickhass.feature.slideover.rest

import fr.outadoc.quickhass.preferences.PreferenceRepository
import fr.outadoc.quickhass.rest.AccessTokenInterceptor
import fr.outadoc.quickhass.rest.AccessTokenProvider
import fr.outadoc.quickhass.rest.AltBaseUrlInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class RestClient<T>(
    private val type: Class<T>,
    loggingInterceptor: HttpLoggingInterceptor,
    accessTokenProvider: AccessTokenProvider,
    prefs: PreferenceRepository
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(AccessTokenInterceptor(accessTokenProvider))
        .addInterceptor(AltBaseUrlInterceptor(prefs))
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

        inline fun <reified T> create(
            loggingInterceptor: HttpLoggingInterceptor,
            accessTokenProvider: AccessTokenProvider,
            prefs: PreferenceRepository
        ): T =
            RestClient(T::class.java, loggingInterceptor, accessTokenProvider, prefs).api
    }
}