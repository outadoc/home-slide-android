package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.feature.slideover.model.State
import java.text.DecimalFormat

class SensorEntity(state: State) : Entity(state, "eye".toIcon()!!) {

    private val decFormatter = DecimalFormat("#.#")

    override val formattedState: String?
        get() = stateStr.toFloatOrNull()?.let { dec ->
            "${decFormatter.format(dec)} ${additionalAttributes.unit ?: ""}".trim()
        }
}