package fr.outadoc.homeslide.hassapi.repository

import fr.outadoc.homeslide.hassapi.api.model.Action
import fr.outadoc.homeslide.hassapi.api.model.EntityState
import fr.outadoc.homeslide.hassapi.api.model.Service
import fr.outadoc.homeslide.hassapi.api.model.entity.Entity
import fr.outadoc.homeslide.hassapi.model.PersistedEntity
import fr.outadoc.homeslide.hassapi.model.Tile

interface EntityRepository {

    suspend fun getEntityTiles(): Result<List<Tile<Entity>>>

    suspend fun getServices(): Result<List<Service>>

    suspend fun callService(action: Action): Result<List<EntityState>>

    suspend fun saveEntityListState(entities: List<PersistedEntity>)
}