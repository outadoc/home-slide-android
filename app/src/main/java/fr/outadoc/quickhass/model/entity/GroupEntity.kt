package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.model.EntityState

class GroupEntity(state: EntityState) : BaseEntity(state, "google-circles-communities".toIcon()) {

    companion object {
        const val DOMAIN = "group"
    }
}