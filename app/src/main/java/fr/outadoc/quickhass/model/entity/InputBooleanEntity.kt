package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.model.EntityState

class InputBooleanEntity(state: EntityState) : BinaryEntity(state, "dip-switch".toIcon()) {

    companion object {
        const val DOMAIN = "input_boolean"
    }
}