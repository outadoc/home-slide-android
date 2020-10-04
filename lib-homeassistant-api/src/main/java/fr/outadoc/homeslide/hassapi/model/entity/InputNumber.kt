package fr.outadoc.homeslide.hassapi.model.entity

import android.content.Context
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.base.BaseEntity
import fr.outadoc.mdi.toIcon
import java.text.DecimalFormat

class InputNumber(state: EntityState) : BaseEntity(state, "import".toIcon()) {

    companion object {
        const val DOMAIN = "input_number"
    }

    private val decFormatter = DecimalFormat("#.#")

    override fun getFormattedState(context: Context): String? {
        val decimalState = stateStr.toFloatOrNull()
        val min = state.attributes.min
        val max = state.attributes.max

        return if (decimalState != null && min != null && max != null) {
            val percentage = (decimalState - min) / (max - min) * 100
            "${decFormatter.format(percentage)} %"
        } else if (decimalState != null) {
            "${decFormatter.format(decimalState)} ${additionalAttributes.unit ?: ""}".trim()
        } else {
            stateStr
        }
    }
}
