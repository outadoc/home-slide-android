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
    protected val stateStr: String = state.state

    @StringDomain
    val domain: String = state.domain

    val icon: IconValue =
        state.attributes.icon?.toIcon() ?: defaultIcon

    val friendlyName: String? = state.attributes.friendlyName

    val isVisible: Boolean = !state.attributes.isHidden

    open val isEnabled: Boolean = true

    open val isOn: Boolean = false

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

abstract class BinaryEntity(state: State, icon: IconValue) : Entity(state, icon) {
    override val isOn: Boolean = stateStr == "on"
}

class GenericEntity(state: State) : Entity(state, IconValue.ANDROID)

class LightEntity(state: State) : BinaryEntity(state, IconValue.LIGHTBULB)

class SwitchEntity(state: State) : BinaryEntity(state, IconValue.POWER_PLUG)

class CoverEntity(state: State) : Entity(state, IconValue.WINDOW_OPEN) {
    override val isOn: Boolean = stateStr == "open"
}

class PersonEntity(state: State) : Entity(state, IconValue.ACCOUNT)

class SunEntity(state: State) : Entity(state, IconValue.WEATHER_SUNNY)


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
