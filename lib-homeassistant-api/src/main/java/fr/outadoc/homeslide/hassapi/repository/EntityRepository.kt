package fr.outadoc.homeslide.hassapi.repository

import fr.outadoc.homeslide.hassapi.model.Action
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.PersistedEntity
import fr.outadoc.homeslide.hassapi.model.Service
import fr.outadoc.homeslide.hassapi.model.Tile
import fr.outadoc.homeslide.hassapi.model.entity.Entity

interface EntityRepository {

    suspend fun getEntityTiles(): Result<List<Tile<Entity>>>

    suspend fun getServices(): Result<List<Service>>

    suspend fun callService(action: Action): Result<List<EntityState>>

    suspend fun saveEntityListState(entities: List<PersistedEntity>)
}