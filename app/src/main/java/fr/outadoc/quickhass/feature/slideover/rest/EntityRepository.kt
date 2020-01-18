package fr.outadoc.quickhass.feature.slideover.rest

import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.model.EntityState
import fr.outadoc.quickhass.model.Service
import fr.outadoc.quickhass.model.entity.Entity

interface EntityRepository {

    suspend fun getEntities(): Result<List<Entity>>

    suspend fun getServices(): Result<List<Service>>

    suspend fun callService(action: Action): Result<List<EntityState>>
}