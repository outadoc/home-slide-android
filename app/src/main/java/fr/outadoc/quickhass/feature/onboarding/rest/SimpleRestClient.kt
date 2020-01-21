package fr.outadoc.quickhass.feature.onboarding.rest

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class SimpleRestClient<T>(private val type: Class<T>, loggingInterceptor: HttpLoggingInterceptor) {

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit
        get() = Retrofit.Builder()
            .client(client)
            .baseUrl(PLACEHOLDER_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    val api: T
        get() = retrofit.create(type)

    companion object {
        const val PLACEHOLDER_BASE_URL = "http://localhost/"

        inline fun <reified T> create(loggingInterceptor: HttpLoggingInterceptor): T =
            SimpleRestClient(T::class.java, loggingInterceptor).api
    }
}