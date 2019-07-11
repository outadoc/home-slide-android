package fr.outadoc.quickhass.rest

import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.model.Service
import fr.outadoc.quickhass.model.State
import retrofit2.Response

interface EntityRepository {

    suspend fun getStates(): Response<List<State>>

    suspend fun getServices(): Response<List<Service>>

    suspend fun callService(action: Action): Response<List<State>>
}