package fr.outadoc.homeslide.hassapi.factory

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.Automation
import fr.outadoc.homeslide.hassapi.model.entity.BinarySensor
import fr.outadoc.homeslide.hassapi.model.entity.Climate
import fr.outadoc.homeslide.hassapi.model.entity.Cover
import fr.outadoc.homeslide.hassapi.model.entity.GenericEntity
import fr.outadoc.homeslide.hassapi.model.entity.Group
import fr.outadoc.homeslide.hassapi.model.entity.InputBoolean
import fr.outadoc.homeslide.hassapi.model.entity.Light
import fr.outadoc.homeslide.hassapi.model.entity.MediaPlayer
import fr.outadoc.homeslide.hassapi.model.entity.Person
import fr.outadoc.homeslide.hassapi.model.entity.Script
import fr.outadoc.homeslide.hassapi.model.entity.Sensor
import fr.outadoc.homeslide.hassapi.model.entity.Sun
import fr.outadoc.homeslide.hassapi.model.entity.Switch
import fr.outadoc.homeslide.hassapi.model.entity.Weather

object EntityFactory {

    fun create(state: EntityState) = when (state.domain) {
        Light.DOMAIN -> Light(
            state
        )
        Cover.DOMAIN -> Cover(
            state
        )
        Person.DOMAIN -> Person(
            state
        )
        Sun.DOMAIN -> Sun(
            state
        )
        Switch.DOMAIN -> Switch(
            state
        )
        Sensor.DOMAIN -> Sensor(
            state
        )
        Script.DOMAIN -> Script(
            state
        )
        Automation.DOMAIN -> Automation(
            state
        )
        Group.DOMAIN -> Group(
            state
        )
        Climate.DOMAIN -> Climate(
            state
        )
        MediaPlayer.DOMAIN -> MediaPlayer(
            state
        )
        Weather.DOMAIN -> Weather(
            state
        )
        InputBoolean.DOMAIN -> InputBoolean(
            state
        )
        BinarySensor.DOMAIN -> BinarySensor(
            state
        )
        else -> GenericEntity(state)
    }
}