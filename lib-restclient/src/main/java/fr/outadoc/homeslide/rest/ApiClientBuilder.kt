package fr.outadoc.homeslide.rest

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class ApiClientBuilder<T>(
    private val type: Class<T>,
    private val parserFactory: Converter.Factory,
    accessTokenProvider: AccessTokenProvider,
    configProvider: AltBaseUrlInterceptorConfigProvider
) {
    private val clientBuilder = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(
            AccessTokenInterceptor(
                accessTokenProvider
            )
        )
        .addInterceptor(
            AltBaseUrlInterceptor(configProvider)
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
            configProvider: AltBaseUrlInterceptorConfigProvider
        ): ApiClientBuilder<T> =
            ApiClientBuilder(
                T::class.java,
                parserFactory,
                accessTokenProvider,
                configProvider
            )
    }
}