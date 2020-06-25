package fr.outadoc.homeslide.rest

import fr.outadoc.homeslide.rest.auth.AccessTokenAuthenticator
import fr.outadoc.homeslide.rest.auth.AccessTokenInterceptor
import fr.outadoc.homeslide.rest.auth.AccessTokenProvider
import fr.outadoc.homeslide.rest.baseurl.AltBaseUrlInterceptor
import fr.outadoc.homeslide.rest.baseurl.BaseUrlConfigProvider
import fr.outadoc.homeslide.rest.util.PLACEHOLDER_BASE_URL
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit

class ApiClientBuilder<T>(
    private val type: Class<T>,
    private val parserFactory: Converter.Factory,
    accessTokenProvider: AccessTokenProvider,
    configProvider: BaseUrlConfigProvider
) {
    private val clientBuilder = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .authenticator(
            AccessTokenAuthenticator(
                accessTokenProvider
            )
        )
        .addInterceptor(
            AccessTokenInterceptor(
                accessTokenProvider
            )
        )
        .addInterceptor(
            AltBaseUrlInterceptor(
                configProvider
            )
        )

    fun addInterceptor(interceptor: Interceptor) =
        apply { clientBuilder.addInterceptor(interceptor) }

    fun build(): T = Retrofit.Builder()
        .baseUrl(PLACEHOLDER_BASE_URL)
        .client(clientBuilder.build())
        .addConverterFactory(parserFactory)
        .build()
        .create(type)

    companion object {
        const val CONNECT_TIMEOUT_SECONDS = 3L

        inline fun <reified T> newBuilder(
            parserFactory: Converter.Factory,
            accessTokenProvider: AccessTokenProvider,
            configProvider: BaseUrlConfigProvider
        ): ApiClientBuilder<T> =
            ApiClientBuilder(
                T::class.java,
                parserFactory,
                accessTokenProvider,
                configProvider
            )
    }
}
