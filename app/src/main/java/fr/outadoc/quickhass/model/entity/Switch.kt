package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.model.EntityState

class Switch(state: EntityState) : ABinaryEntity(state, "power-plug".toIcon()) {

    companion object {
        const val DOMAIN = "switch"
    }
}