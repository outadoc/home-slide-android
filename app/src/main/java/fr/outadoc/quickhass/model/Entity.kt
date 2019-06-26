package fr.outadoc.quickhass.model

import fr.outadoc.quickhass.model.annotation.StringDomain
import fr.outadoc.quickhass.model.annotation.StringEntityId
import fr.outadoc.quickhass.model.annotation.StringIcon
import fr.outadoc.quickhass.model.annotation.StringState
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder.IconValue

sealed class Entity(private val state: State, private val defaultIcon: IconValue) {

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
    open val fallbackIcon: IconValue? = null

    val icon: IconValue
        get() = state.attributes.icon?.toIcon() ?: fallbackIcon ?: defaultIcon

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

private fun @StringIcon String.toIcon(): IconValue? {
    return try {
        IconValue.valueOf(
            takeLastWhile { it != ':' }
                .replace('-', '_')
                .toUpperCase()
        )
    } catch (e: IllegalArgumentException) {
        null
    }
}

abstract class BinaryEntity(state: State, icon: IconValue) : Entity(state, icon) {
    override val isOn: Boolean = stateStr == "on"

    override val primaryAction = Action(
        "homeassistant", "toggle", entityId
    )
}

class GenericEntity(state: State) : Entity(state, IconValue.BOOKMARK)

class LightEntity(state: State) : BinaryEntity(state, IconValue.LIGHTBULB)

class SwitchEntity(state: State) : BinaryEntity(state, IconValue.POWER_PLUG)

class CoverEntity(state: State) : Entity(state, IconValue.WINDOW_OPEN) {
    override val isOn: Boolean = stateStr == "open"
}

class PersonEntity(state: State) : Entity(state, IconValue.ACCOUNT)

class SunEntity(state: State) : Entity(state, IconValue.WEATHER_SUNNY)

class SensorEntity(state: State) : Entity(state, IconValue.EYE)

class ScriptEntity(state: State) : Entity(state, IconValue.FILE_DOCUMENT)

class AutomationEntity(state: State) : Entity(state, IconValue.PLAYLIST_PLAY)

class GroupEntity(state: State) : Entity(state, IconValue.GOOGLE_CIRCLES_COMMUNITIES)

class ClimateEntity(state: State) : Entity(state, IconValue.THERMOSTAT)

class MediaPlayerEntity(state: State) : Entity(state, IconValue.CAST)

class WeatherEntity(state: State) : Entity(state, IconValue.WEATHER_CLOUDY) {
    override val fallbackIcon: IconValue?
        get() = when (stateStr) {
            "clear-night" -> IconValue.WEATHER_NIGHT
            "cloudy" -> IconValue.WEATHER_CLOUDY
            "fog" -> IconValue.WEATHER_FOG
            "hail" -> IconValue.WEATHER_HAIL
            "lightning" -> IconValue.WEATHER_LIGHTNING
            "lightning-rainy" -> IconValue.WEATHER_LIGHTNING_RAINY
            "partlycloudy" -> IconValue.WEATHER_PARTLYCLOUDY
            "pouring" -> IconValue.WEATHER_POURING
            "rainy" -> IconValue.WEATHER_RAINY
            "snowy" -> IconValue.WEATHER_SNOWY
            "snowy-rainy" -> IconValue.WEATHER_SNOWY_RAINY
            "sunny" -> IconValue.WEATHER_SUNNY
            "windy" -> IconValue.WEATHER_WINDY
            "windy-variant" -> IconValue.WEATHER_WINDY_VARIANT
            else -> null
        }
}

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
        else -> GenericEntity(state)
    }
}
