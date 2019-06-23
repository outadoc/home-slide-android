package fr.outadoc.quickhass.rest

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


abstract class BaseServer<T>(type: Class<T>) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val api: T = retrofit.create(type)

    companion object {
        private const val BASE_URL = "http://domos.local:8123/"
    }
}