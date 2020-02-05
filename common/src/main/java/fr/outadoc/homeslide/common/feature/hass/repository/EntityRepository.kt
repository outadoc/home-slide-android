package fr.outadoc.homeslide.common.feature.hass.repository

import fr.outadoc.homeslide.common.feature.hass.model.Action
import fr.outadoc.homeslide.common.feature.hass.model.EntityState
import fr.outadoc.homeslide.common.feature.hass.model.Service
import fr.outadoc.homeslide.common.feature.hass.model.Tile
import fr.outadoc.homeslide.common.feature.hass.model.entity.Entity

interface EntityRepository {

    suspend fun getEntityTiles(): Result<List<Tile<Entity>>>

    suspend fun getServices(): Result<List<Service>>

    suspend fun callService(action: Action): Result<List<EntityState>>
}