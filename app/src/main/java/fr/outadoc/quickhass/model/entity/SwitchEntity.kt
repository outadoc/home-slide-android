package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.model.EntityState

class SwitchEntity(state: EntityState) : BinaryEntity(state, "power-plug".toIcon()) {

    companion object {
        const val DOMAIN = "switch"
    }
}