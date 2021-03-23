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

import okhttp3.internal.platform.Platform
import java.security.GeneralSecurityException
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

internal fun X509TrustManager.createSocketFactory() = try {
    Platform.get()
        .sslContext
        .apply { init(null, arrayOf<TrustManager>(this@createSocketFactory), null) }
        .socketFactory
} catch (e: GeneralSecurityException) {
    // The system has no TLS. Just give up.
    throw AssertionError("No System TLS", e)
}
