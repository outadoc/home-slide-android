package fr.outadoc.homeslide.common.feature.hass.model.entity

import fr.outadoc.homeslide.common.feature.hass.model.EntityState
import fr.outadoc.mdi.toIcon

class GenericEntity(state: EntityState) : ABaseEntity(state, "bookmark".toIcon())