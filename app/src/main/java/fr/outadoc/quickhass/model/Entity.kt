package fr.outadoc.quickhass.model

import fr.outadoc.quickhass.model.annotation.StringDomain
import fr.outadoc.quickhass.model.annotation.StringEntityId
import fr.outadoc.quickhass.model.annotation.StringIcon
import fr.outadoc.quickhass.model.annotation.StringState
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder.IconValue

sealed class Entity(state: State, defaultIcon: IconValue) {

    @StringEntityId
    val entityId: String = state.entityId

    @StringState
    val stateStr: String = state.state

    @StringDomain
    val domain: String = state.domain

    val attributes: AttributeSet = state.attributes

    val icon: IconValue =
        attributes.icon?.toIcon() ?: defaultIcon

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
        IconValue.valueOf(this.takeLastWhile { it != ':' }.toUpperCase())
    } catch (e: IllegalArgumentException) {
        null
    }
}

class GenericEntity(state: State) : Entity(state, IconValue.ANDROID)
class LightEntity(state: State) : Entity(state, IconValue.LIGHTBULB)
class CoverEntity(state: State) : Entity(state, IconValue.WINDOW_OPEN)
class PersonEntity(state: State) : Entity(state, IconValue.ACCOUNT)
class SunEntity(state: State) : Entity(state, IconValue.WEATHER_SUNNY)
class SwitchEntity(state: State) : Entity(state, IconValue.POWER_PLUG)

object EntityFactory {

    fun create(state: State): Entity = when (state.domain) {
        "light" -> LightEntity(state)
        "cover" -> CoverEntity(state)
        "person" -> PersonEntity(state)
        "sun" -> SunEntity(state)
        "switch" -> SwitchEntity(state)
        else -> GenericEntity(state)
    }
}
