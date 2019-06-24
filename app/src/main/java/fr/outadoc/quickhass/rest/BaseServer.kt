package fr.outadoc.quickhass.rest

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


abstract class BaseServer<T>(type: Class<T>) {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val newHeaders = chain.request()
            .headers()
            .newBuilder()
            .set("Authorization", "Bearer $TOKEN")
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

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val api: T = retrofit.create(type)

    companion object {
        private const val BASE_URL = "https://***REMOVED***"
        private const val TOKEN =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI1YTlmZjgwZWU0NDA0MDlkOGZmYzg1OWVkMmU1NjU0YSIsImlhdCI6MTU2MTMwODE4NywiZXhwIjoxODc2NjY4MTg3fQ.ehSUIWsiCnPQCs6J21fUp2jl9DYw9oYNQNNYveTEZYg"
    }
}