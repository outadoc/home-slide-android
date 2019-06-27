package fr.outadoc.quickhass.model

import fr.outadoc.mdi.FontIcon
import fr.outadoc.mdi.IconMap
import fr.outadoc.mdi.IconStringRef
import fr.outadoc.quickhass.model.annotation.StringDomain
import fr.outadoc.quickhass.model.annotation.StringEntityId
import fr.outadoc.quickhass.model.annotation.StringState

sealed class Entity(private val state: State, private val defaultIcon: FontIcon) {

    @StringEntityId
    val entityId: String = state.entityId

    @StringState
    protected val stateStr: String = state.state

    @StringDomain
    val domain: String = state.domain

    val friendlyName: String? = state.attributes.friendlyName

    val isVisible: Boolean = !state.attributes.isHidden

    val isEnabled: Boolean
        get() = primaryAction != null

    open val isOn: Boolean = false

    open val primaryAction: Action? = null

    /**
     * Can be overridden by children to provide a contextual icon.
     * e.g. different icon for different weather
     */
    open val fallbackIcon: FontIcon? = null

    val icon: FontIcon
        get() = state.attributes.icon?.toIcon() ?: fallbackIcon ?: defaultIcon

    val additionalAttributes = state.attributes

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Entity

        if (entityId != other.entityId) return false

        return true
    }

    override fun hashCode(): Int {
        return entityId.hashCode()
    }
}

private fun @IconStringRef String.toIcon(): FontIcon? {
    return IconMap.getIcon(this)
}

abstract class BinaryEntity(state: State, defaultIcon: FontIcon) : Entity(state, defaultIcon) {
    override val isOn: Boolean = stateStr == "on"

    override val primaryAction = Action(
        "homeassistant", "toggle", entityId
    )
}

class GenericEntity(state: State) : Entity(state, "bookmark".toIcon()!!)

class LightEntity(state: State) : BinaryEntity(state, "lightbulb".toIcon()!!)

class SwitchEntity(state: State) : BinaryEntity(state, "power-plug".toIcon()!!)

class CoverEntity(state: State) : Entity(state, "window-open".toIcon()!!) {
    override val isOn: Boolean = stateStr == "open"

    override val primaryAction: Action?
        get() = when (stateStr) {
            "open" -> Action("cover", "close_cover", entityId)
            "closed" -> Action("cover", "open_cover", entityId)
            else -> null
        }
}

class PersonEntity(state: State) : Entity(state, "account".toIcon()!!)

class SunEntity(state: State) : Entity(state, "weather-sunny".toIcon()!!)

class SensorEntity(state: State) : Entity(state, "eye".toIcon()!!)

class ScriptEntity(state: State) : Entity(state, "file-document".toIcon()!!) {

    private val scriptName = entityId.takeLastWhile { it != '.' }

    override val primaryAction: Action?
        get() = Action("script", scriptName, entityId)
}

class AutomationEntity(state: State) : Entity(state, "playlist-play".toIcon()!!)

class GroupEntity(state: State) : Entity(state, "google-circles-communities".toIcon()!!)

class ClimateEntity(state: State) : Entity(state, "thermostat".toIcon()!!) {
    override val isOn: Boolean
        get() = stateStr != "off"

    override val primaryAction: Action?
        get() = when (stateStr) {
            "off" -> Action("climate", "turn_on", entityId)
            else -> Action("climate", "turn_off", entityId)
        }
}

class MediaPlayerEntity(state: State) : BinaryEntity(state, "cast".toIcon()!!)

class WeatherEntity(state: State) : Entity(state, "weather-cloudy".toIcon()!!) {

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
        }?.toIcon()
}

class InputBooleanEntity(state: State) : BinaryEntity(state, "dip-switch".toIcon()!!)

object EntityFactory {

    fun create(state: State): Entity = when (state.domain) {
        "light" -> LightEntity(state)
        "cover" -> CoverEntity(state)
        "person" -> PersonEntity(state)
        "sun" -> SunEntity(state)
        "switch" -> SwitchEntity(state)
        "sensor" -> SensorEntity(state)
        "script" -> ScriptEntity(state)
        "automation" -> AutomationEntity(state)
        "group" -> GroupEntity(state)
        "climate" -> ClimateEntity(state)
        "media_player" -> MediaPlayerEntity(state)
        "weather" -> WeatherEntity(state)
        "input_boolean" -> InputBooleanEntity(state)
        else -> GenericEntity(state)
    }
}
