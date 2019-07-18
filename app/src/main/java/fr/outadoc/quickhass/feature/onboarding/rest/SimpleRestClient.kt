package fr.outadoc.quickhass.feature.onboarding.rest

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class SimpleRestClient<T>(private val type: Class<T>) {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    private val retrofit: Retrofit
        get() = Retrofit.Builder()
                .client(client)
                .baseUrl("http://example.com/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

    val api: T
        get() = retrofit.create(type)

    companion object {
        inline fun <reified T> create(): T =
                SimpleRestClient(T::class.java).api
    }
}