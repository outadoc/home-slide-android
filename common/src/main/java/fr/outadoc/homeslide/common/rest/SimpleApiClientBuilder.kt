package fr.outadoc.homeslide.common.rest

import fr.outadoc.homeslide.rest.util.PLACEHOLDER_BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit

class SimpleApiClientBuilder<T>(
    private val type: Class<T>,
    private val parserFactory: Converter.Factory
) {
    private val clientBuilder = OkHttpClient.Builder()

    fun addInterceptor(interceptor: Interceptor) =
        apply { clientBuilder.addInterceptor(interceptor) }

    fun build(): T = Retrofit.Builder()
        .baseUrl(PLACEHOLDER_BASE_URL)
        .client(clientBuilder.build())
        .addConverterFactory(parserFactory)
        .build()
        .create(type)

    companion object {
        inline fun <reified T> newBuilder(
            parserFactory: Converter.Factory
        ): SimpleApiClientBuilder<T> =
            SimpleApiClientBuilder(
                T::class.java,
                parserFactory
            )
    }
}