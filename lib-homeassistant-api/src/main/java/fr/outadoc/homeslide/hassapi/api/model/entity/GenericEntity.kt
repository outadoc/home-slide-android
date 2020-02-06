package fr.outadoc.homeslide.hassapi.api.model.entity

import fr.outadoc.homeslide.hassapi.api.model.EntityState
import fr.outadoc.mdi.toIcon

class GenericEntity(state: EntityState) : ABaseEntity(state, "bookmark".toIcon())