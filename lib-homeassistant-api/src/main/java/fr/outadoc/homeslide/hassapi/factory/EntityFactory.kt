/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.outadoc.homeslide.hassapi.factory

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.Automation
import fr.outadoc.homeslide.hassapi.model.entity.BinarySensor
import fr.outadoc.homeslide.hassapi.model.entity.Calendar
import fr.outadoc.homeslide.hassapi.model.entity.Climate
import fr.outadoc.homeslide.hassapi.model.entity.Cover
import fr.outadoc.homeslide.hassapi.model.entity.Fan
import fr.outadoc.homeslide.hassapi.model.entity.GenericEntity
import fr.outadoc.homeslide.hassapi.model.entity.Group
import fr.outadoc.homeslide.hassapi.model.entity.InputBoolean
import fr.outadoc.homeslide.hassapi.model.entity.InputNumber
import fr.outadoc.homeslide.hassapi.model.entity.Light
import fr.outadoc.homeslide.hassapi.model.entity.Lock
import fr.outadoc.homeslide.hassapi.model.entity.MediaPlayer
import fr.outadoc.homeslide.hassapi.model.entity.Person
import fr.outadoc.homeslide.hassapi.model.entity.Scene
import fr.outadoc.homeslide.hassapi.model.entity.Script
import fr.outadoc.homeslide.hassapi.model.entity.Sensor
import fr.outadoc.homeslide.hassapi.model.entity.Sun
import fr.outadoc.homeslide.hassapi.model.entity.Switch
import fr.outadoc.homeslide.hassapi.model.entity.Vacuum
import fr.outadoc.homeslide.hassapi.model.entity.Weather

object EntityFactory {

    fun create(state: EntityState) = when (state.domain) {
        Automation.DOMAIN -> Automation(state)
        BinarySensor.DOMAIN -> BinarySensor(state)
        Calendar.DOMAIN -> Calendar(state)
        Climate.DOMAIN -> Climate(state)
        Cover.DOMAIN -> Cover(state)
        Fan.DOMAIN -> Fan(state)
        Group.DOMAIN -> Group(state)
        InputBoolean.DOMAIN -> InputBoolean(state)
        InputNumber.DOMAIN -> InputNumber(state)
        Light.DOMAIN -> Light(state)
        Lock.DOMAIN -> Lock(state)
        MediaPlayer.DOMAIN -> MediaPlayer(state)
        Person.DOMAIN -> Person(state)
        Scene.DOMAIN -> Scene(state)
        Script.DOMAIN -> Script(state)
        Sensor.DOMAIN -> Sensor(state)
        Sun.DOMAIN -> Sun(state)
        Switch.DOMAIN -> Switch(state)
        Vacuum.DOMAIN -> Vacuum(state)
        Weather.DOMAIN -> Weather(state)
        else -> GenericEntity(state)
    }
}
