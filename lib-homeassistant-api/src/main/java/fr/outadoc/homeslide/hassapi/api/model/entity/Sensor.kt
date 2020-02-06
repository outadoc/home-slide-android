package fr.outadoc.homeslide.hassapi.api.model.entity

import android.content.Context
import fr.outadoc.homeslide.hassapi.api.model.EntityState
import fr.outadoc.mdi.toIcon
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