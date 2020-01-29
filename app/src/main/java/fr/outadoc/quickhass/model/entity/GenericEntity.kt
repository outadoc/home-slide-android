package fr.outadoc.quickhass.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.model.EntityState

class GenericEntity(state: EntityState) : ABaseEntity(state, "bookmark".toIcon())