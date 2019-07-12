package fr.outadoc.quickhass.rest

import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.model.Service
import fr.outadoc.quickhass.model.State

interface EntityRepository {

    suspend fun getStates(): Result<List<State>>

    suspend fun getServices(): Result<List<Service>>

    suspend fun callService(action: Action): Result<List<State>>
}