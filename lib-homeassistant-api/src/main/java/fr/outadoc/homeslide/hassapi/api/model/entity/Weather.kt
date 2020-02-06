package fr.outadoc.homeslide.hassapi.api.model.entity

import fr.outadoc.homeslide.hassapi.api.model.EntityState
import fr.outadoc.mdi.FontIcon
import fr.outadoc.mdi.toIcon
import fr.outadoc.mdi.toIconOrNull

class Weather(state: EntityState) : ABaseEntity(state, "weather-cloudy".toIcon()) {

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