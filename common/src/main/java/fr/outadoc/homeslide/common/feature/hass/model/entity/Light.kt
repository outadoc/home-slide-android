package fr.outadoc.homeslide.common.feature.hass.model.entity

import fr.outadoc.homeslide.common.feature.hass.model.EntityState
import fr.outadoc.mdi.toIcon

class Light(state: EntityState) : ABinaryEntity(state, "lightbulb".toIcon()) {

    val brightness: Int?
        get() = additionalAttributes.brightness?.let { brightness ->
            (brightness / 256f * 100).toInt().coerceIn(0, 100)
        }

    companion object {
        const val DOMAIN = "light"
    }
}