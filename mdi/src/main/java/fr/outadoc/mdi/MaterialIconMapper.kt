package fr.outadoc.mdi

interface MaterialIconMapper {
    fun getIcon(@IconStringRef iconName: String): FontIcon?
}
