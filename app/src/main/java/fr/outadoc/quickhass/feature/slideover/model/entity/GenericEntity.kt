package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.feature.slideover.model.EntityState

class GenericEntity(state: EntityState) : BaseEntity(state, "bookmark".toIcon())