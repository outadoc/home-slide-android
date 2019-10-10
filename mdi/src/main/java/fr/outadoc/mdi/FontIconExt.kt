package fr.outadoc.mdi

import java.util.*

fun @IconStringRef String.toIconOrNull(): FontIcon? {
    val cleanup = takeLastWhile { it != ':' }.toLowerCase(Locale.ROOT)
    return MaterialIconLocator.instance?.getIcon(cleanup)
}

fun @IconStringRef String.toIcon() =
    toIconOrNull() ?: throw NoSuchIconException(this)