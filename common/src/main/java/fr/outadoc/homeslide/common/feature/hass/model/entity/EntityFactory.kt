package fr.outadoc.homeslide.common.feature.hass.model.entity

import fr.outadoc.homeslide.common.feature.hass.model.EntityState

object EntityFactory {

    fun create(state: EntityState) = when (state.domain) {
        Light.DOMAIN -> Light(state)
        Cover.DOMAIN -> Cover(state)
        Person.DOMAIN -> Person(state)
        Sun.DOMAIN -> Sun(state)
        Switch.DOMAIN -> Switch(state)
        Sensor.DOMAIN -> Sensor(state)
        Script.DOMAIN -> Script(state)
        Automation.DOMAIN -> Automation(state)
        Group.DOMAIN -> Group(state)
        Climate.DOMAIN -> Climate(state)
        MediaPlayer.DOMAIN -> MediaPlayer(state)
        Weather.DOMAIN -> Weather(state)
        InputBoolean.DOMAIN -> InputBoolean(state)
        BinarySensor.DOMAIN -> BinarySensor(state)
        else -> GenericEntity(state)
    }
}