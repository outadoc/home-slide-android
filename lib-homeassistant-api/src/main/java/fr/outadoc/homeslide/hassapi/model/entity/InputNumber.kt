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
