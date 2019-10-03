package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.feature.slideover.model.EntityState
import java.text.DecimalFormat

class SensorEntity(state: EntityState) : BaseEntity(state, "eye".toIcon()!!) {

    companion object {
        const val DOMAIN = "sensor"
    }

    private val decFormatter = DecimalFormat("#.#")

    override val formattedState: String?
        get() = stateStr.toFloatOrNull()?.let { dec ->
            "${decFormatter.format(dec)} ${additionalAttributes.unit ?: ""}".trim()
        }
}