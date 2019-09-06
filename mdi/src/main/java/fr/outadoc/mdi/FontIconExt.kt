package fr.outadoc.mdi

fun @IconStringRef String.toIcon(): FontIcon? {
    return IconMap.getIcon(takeLastWhile { it != ':' }.toLowerCase())
}
