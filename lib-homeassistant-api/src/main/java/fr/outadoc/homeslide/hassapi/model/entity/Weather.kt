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

package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.base.BaseEntity
import fr.outadoc.mdi.common.MdiFontIcon
import fr.outadoc.mdi.toIcon
import fr.outadoc.mdi.toIconOrNull

class Weather(state: EntityState) : BaseEntity(state, "weather-cloudy".toIcon()) {

    companion object {
        const val DOMAIN = "weather"
    }

    override val fallbackIcon: MdiFontIcon?
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
