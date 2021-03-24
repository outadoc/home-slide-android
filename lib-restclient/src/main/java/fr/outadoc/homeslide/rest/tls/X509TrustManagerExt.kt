/*
 * Copyright 2021 Baptiste Candellier
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

package fr.outadoc.homeslide.rest.tls

import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

fun X509TrustManager.createSocketFactory(): SSLSocketFactory =
    try {
        getDefaultSslContext()
            .apply { init(null, arrayOf<TrustManager>(this@createSocketFactory), SecureRandom()) }
            .socketFactory
    } catch (e: GeneralSecurityException) {
        // The system has no TLS. Just give up.
        throw AssertionError("No System TLS", e)
    }

private fun getDefaultSslContext(): SSLContext =
    try {
        SSLContext.getInstance("TLS")
    } catch (e: NoSuchAlgorithmException) {
        throw IllegalStateException("No TLS provider", e)
    }

fun getDefaultTrustManager(): X509TrustManager =
    try {
        val trustManagers = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        ).apply {
            init(null as KeyStore?)
        }.trustManagers

        if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
            throw IllegalStateException(
                "Unexpected default trust managers: ${trustManagers.joinToString()}"
            )
        }

        trustManagers[0] as X509TrustManager
    } catch (e: GeneralSecurityException) {
        // The system has no TLS. Just give up.
        throw AssertionError("No System TLS", e)
    }
