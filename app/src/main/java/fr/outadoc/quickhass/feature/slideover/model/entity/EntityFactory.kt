package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.quickhass.feature.slideover.model.EntityState

object EntityFactory {

    fun create(state: EntityState) = when (state.domain) {
        LightEntity.DOMAIN -> LightEntity(state)
        CoverEntity.DOMAIN -> CoverEntity(state)
        PersonEntity.DOMAIN -> PersonEntity(state)
        SunEntity.DOMAIN -> SunEntity(state)
        SwitchEntity.DOMAIN -> SwitchEntity(state)
        SensorEntity.DOMAIN -> SensorEntity(state)
        ScriptEntity.DOMAIN -> ScriptEntity(state)
        AutomationEntity.DOMAIN -> AutomationEntity(state)
        GroupEntity.DOMAIN -> GroupEntity(state)
        ClimateEntity.DOMAIN -> ClimateEntity(state)
        MediaPlayerEntity.DOMAIN -> MediaPlayerEntity(state)
        WeatherEntity.DOMAIN -> WeatherEntity(state)
        InputBooleanEntity.DOMAIN -> InputBooleanEntity(state)
        else -> GenericEntity(state)
    }
}