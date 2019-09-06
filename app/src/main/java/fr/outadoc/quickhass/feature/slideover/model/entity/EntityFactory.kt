package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.quickhass.feature.slideover.model.State

object EntityFactory {

    fun create(state: State) = when (state.domain) {
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