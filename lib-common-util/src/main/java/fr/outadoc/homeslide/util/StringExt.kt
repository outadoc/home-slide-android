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
