package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.model.EntityState

class PersonEntity(state: EntityState) : BaseEntity(state, "account".toIcon()) {

    companion object {
        const val DOMAIN = "person"
    }
}