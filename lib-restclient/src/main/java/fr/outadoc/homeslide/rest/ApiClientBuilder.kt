/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.outadoc.homeslide.rest

import fr.outadoc.homeslide.rest.auth.AccessTokenAuthenticator
import fr.outadoc.homeslide.rest.auth.AccessTokenInterceptor
import fr.outadoc.homeslide.rest.auth.AccessTokenProvider
import fr.outadoc.homeslide.rest.baseurl.AltBaseUrlInterceptor
import fr.outadoc.homeslide.rest.baseurl.BaseUrlProvider
import fr.outadoc.homeslide.rest.tls.TlsConfigurationProvider
import fr.outadoc.homeslide.rest.tls.UnsafeHostnameVerifier
import fr.outadoc.homeslide.rest.tls.UnsafeX509TrustManager
import fr.outadoc.homeslide.rest.tls.createSocketFactory
import fr.outadoc.homeslide.rest.tls.getDefaultHostnameVerifier
import fr.outadoc.homeslide.rest.tls.getDefaultTrustManager
import fr.outadoc.homeslide.rest.util.PLACEHOLDER_BASE_URL
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class ApiClientBuilder<T>(
    private val type: Class<T>,
    private val parserFactory: Converter.Factory,
    accessTokenProvider: AccessTokenProvider,
    baseUrlProvider: BaseUrlProvider,
    tlsConfigurationProvider: TlsConfigurationProvider
) {
    private val unsafeTrustManager = UnsafeX509TrustManager(
        tlsConfigurationProvider,
        delegate = getDefaultTrustManager()
    )

    private val unsafeHostnameVerifier = UnsafeHostnameVerifier(
        tlsConfigurationProvider,
        delegate = getDefaultHostnameVerifier()
    )

    private val connectionPool = ConnectionPool()

    init {
        tlsConfigurationProvider.addCertificateCheckEnabledChangedListener {
            connectionPool.evictAll()
        }
    }

    private val clientBuilder = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .sslSocketFactory(unsafeTrustManager.createSocketFactory(), unsafeTrustManager)
        .hostnameVerifier(unsafeHostnameVerifier)
        .connectionPool(connectionPool)
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
                baseUrlProvider
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
        const val WRITE_TIMEOUT_SECONDS = 10L
        const val READ_TIMEOUT_SECONDS = 60L

        inline fun <reified T> newBuilder(
            parserFactory: Converter.Factory,
            accessTokenProvider: AccessTokenProvider,
            baseUrlProvider: BaseUrlProvider,
            tlsConfigurationProvider: TlsConfigurationProvider
        ): ApiClientBuilder<T> =
            ApiClientBuilder(
                T::class.java,
                parserFactory,
                accessTokenProvider,
                baseUrlProvider,
                tlsConfigurationProvider
            )
    }
}
