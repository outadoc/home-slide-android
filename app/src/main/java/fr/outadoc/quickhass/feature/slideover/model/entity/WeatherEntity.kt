package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.FontIcon
import fr.outadoc.mdi.toIcon
import fr.outadoc.mdi.toIconOrNull
import fr.outadoc.quickhass.feature.slideover.model.EntityState

class WeatherEntity(state: EntityState) : BaseEntity(state, "weather-cloudy".toIcon()) {

    companion object {
        const val DOMAIN = "weather"
    }

    override val fallbackIcon: FontIcon?
        get() = when (stateStr) {
            "clear-night" -> "weather-night"
            "cloudy" -> "weather-cloudy"
            "fog" -> "weather-fog"
            "hail" -> "weather-hail"
            "lightning" -> "weather-lightning"
            "lightning-rainy" -> "weather-lightning-rainy"
            "partlycloudy" -> "weather-partlycloudy"
            "pouring" -> "weather-pouring"
            "rainy" -> "weather-rainy"
            "snowy" -> "weather-snowy"
            "snowy-rainy" -> "weather-snowy-rainy"
            "sunny" -> "weather-sunny"
            "windy" -> "weather-windy"
            "windy-variant" -> "weather-windy-variant"
            else -> null
        }?.toIconOrNull()
}