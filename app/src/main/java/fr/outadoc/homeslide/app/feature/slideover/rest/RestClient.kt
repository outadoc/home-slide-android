package fr.outadoc.homeslide.app.feature.slideover.rest

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.squareup.moshi.Moshi
import fr.outadoc.homeslide.common.rest.AccessTokenInterceptor
import fr.outadoc.homeslide.common.rest.AccessTokenProvider
import fr.outadoc.homeslide.common.rest.AltBaseUrlInterceptor
import fr.outadoc.homeslide.shared.preferences.PreferenceRepository
import fr.outadoc.homeslide.shared.rest.PLACEHOLDER_BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class RestClient<T>(
    private val type: Class<T>,
    private val moshi: Moshi,
    loggingInterceptor: Interceptor,
    chuckerInterceptor: Interceptor,
    accessTokenProvider: AccessTokenProvider,
    prefs: PreferenceRepository
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(
            AccessTokenInterceptor(
                accessTokenProvider
            )
        )
        .addInterceptor(
            AltBaseUrlInterceptor(
                prefs
            )
        )
        .addInterceptor(loggingInterceptor)
        .addInterceptor(chuckerInterceptor)
        .build()

    private val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(PLACEHOLDER_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    val api: T
        get() = retrofit.create(type)

    companion object {
        const val CONNECT_TIMEOUT_SECONDS = 3L

        inline fun <reified T> create(
            moshi: Moshi,
            loggingInterceptor: HttpLoggingInterceptor,
            chuckerInterceptor: ChuckerInterceptor,
            accessTokenProvider: AccessTokenProvider,
            prefs: PreferenceRepository
        ): T =
            RestClient(
                T::class.java,
                moshi,
                loggingInterceptor,
                chuckerInterceptor,
                accessTokenProvider,
                prefs
            ).api
    }
}