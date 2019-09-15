package fr.outadoc.mdi

import java.util.*

fun @IconStringRef String.toIcon() =
    IconMap.getIcon(takeLastWhile { it != ':' }.toLowerCase(Locale.ROOT))
