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

package fr.outadoc.homeslide.util

import androidx.core.net.toUri

fun String?.sanitizeUrl(): String? {
    if (this == null)
        return null

    val str = this
        .trim()
        .ensureProtocol()

    if (str.isEmpty() || str.length < 3) return null

    try {
        str.toUri()
    } catch (ignored: Exception) {
        return null
    }

    if (str.last() == '/') return str.dropLast(1)

    return str
}

fun String.ensureProtocol() = when {
    this.startsWith("http://") || this.startsWith("https://") -> this
    else -> "http://$this"
}
