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

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

class UnsafeHostnameVerifier(
    private val tlsConfigurationProvider: TlsConfigurationProvider,
    private val delegate: HostnameVerifier
) : HostnameVerifier {

    override fun verify(hostname: String?, session: SSLSession?): Boolean {
        return !tlsConfigurationProvider.isCertificateCheckEnabled ||
            delegate.verify(hostname, session)
    }
}
