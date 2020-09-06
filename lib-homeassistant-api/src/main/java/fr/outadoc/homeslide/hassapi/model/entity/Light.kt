package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.base.ToggleableEntity
import fr.outadoc.mdi.toIcon

class Light(state: EntityState) : ToggleableEntity(state, "lightbulb".toIcon()) {

    val brightness: Float?
        get() = if (!supportsFeature(SUPPORT_BRIGHTNESS)) null
        else additionalAttributes.brightness?.let { brightness ->
            (brightness / 256f).coerceIn(0f, 1f)
        } ?: 0f

    companion object {
        const val DOMAIN = "light"

        private const val SUPPORT_BRIGHTNESS = 1
    }
}
