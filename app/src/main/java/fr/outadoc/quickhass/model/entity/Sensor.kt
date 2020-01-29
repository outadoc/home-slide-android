package fr.outadoc.quickhass.model.entity

import android.content.Context
import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.model.EntityState
import java.text.DecimalFormat

class Sensor(state: EntityState) : ABaseEntity(state, "eye".toIcon()) {

    companion object {
        const val DOMAIN = "sensor"
    }

    private val decFormatter = DecimalFormat("#.#")

    override fun getFormattedState(context: Context): String? {
        return stateStr.toFloatOrNull()?.let { dec ->
            "${decFormatter.format(dec)} ${additionalAttributes.unit ?: ""}".trim()
        }
    }
}