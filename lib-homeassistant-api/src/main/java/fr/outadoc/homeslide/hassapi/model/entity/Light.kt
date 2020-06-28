package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.base.ToggleableEntity
import fr.outadoc.mdi.toIcon

class Light(state: EntityState) : ToggleableEntity(state, "lightbulb".toIcon()) {

    val brightness: Int?
        get() = additionalAttributes.brightness?.let { brightness ->
            (brightness / 256f * 100).toInt().coerceIn(0, 100)
        }

    companion object {
        const val DOMAIN = "light"
    }
}
