package fr.outadoc.mdi

import java.util.Locale

fun @IconStringRef String.toIconOrNull(): FontIcon? {
    val cleanup = takeLastWhile { it != ':' }.toLowerCase(Locale.ROOT)
    return MaterialIconLocator.instance?.getIcon(cleanup)
}

fun @IconStringRef String.toIcon() =
    toIconOrNull() ?: throw NoSuchIconException(this)
