package fr.outadoc.quickhass.rest

import okhttp3.Interceptor
import okhttp3.Response

class AccessTokenInterceptor(private val tokenProvider: AccessTokenProvider) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenProvider.token()

        return if (token == null) {
            chain.proceed(chain.request())
        } else {
            val authenticatedRequest = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()

            chain.proceed(authenticatedRequest)
        }
    }
}