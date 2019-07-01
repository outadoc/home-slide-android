package fr.outadoc.quickhass.rest

import fr.outadoc.quickhass.preferences.PreferenceRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


abstract class BaseServer<T>(private val type: Class<T>, private val prefs: PreferenceRepository) {

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

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor).build()

    private val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(prefs.instanceBaseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    val api: T
        get() = retrofit.create(type)
}