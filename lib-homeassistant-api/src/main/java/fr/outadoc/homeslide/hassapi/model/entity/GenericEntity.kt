package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.mdi.toIcon

class GenericEntity(state: EntityState) : ABaseEntity(state, "bookmark".toIcon())