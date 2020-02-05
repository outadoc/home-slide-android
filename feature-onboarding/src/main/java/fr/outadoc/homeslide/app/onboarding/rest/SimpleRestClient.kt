package fr.outadoc.homeslide.app.onboarding.rest

import com.chuckerteam.chucker.api.ChuckerInterceptor
import fr.outadoc.homeslide.shared.rest.PLACEHOLDER_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class SimpleRestClient<T>(
    private val type: Class<T>,
    loggingInterceptor: HttpLoggingInterceptor,
    chuckerInterceptor: ChuckerInterceptor
) {
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(chuckerInterceptor)
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
        inline fun <reified T> create(
            loggingInterceptor: HttpLoggingInterceptor,
            chuckerInterceptor: ChuckerInterceptor
        ): T =
            SimpleRestClient(T::class.java, loggingInterceptor, chuckerInterceptor).api
    }
}