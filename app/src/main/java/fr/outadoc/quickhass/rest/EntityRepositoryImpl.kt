package fr.outadoc.quickhass.rest

import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.model.Service
import fr.outadoc.quickhass.model.State
import fr.outadoc.quickhass.preferences.PreferenceRepository

class EntityRepositoryImpl(prefs: PreferenceRepository) :
    BaseApiRepository<HomeAssistantApi>(HomeAssistantApi::class.java, prefs),
    EntityRepository {

    override suspend fun getStates(): Result<List<State>> =
        wrapResponse { api.getStates() }

    override suspend fun getServices(): Result<List<Service>> =
        wrapResponse { api.getServices() }

    override suspend fun callService(action: Action): Result<List<State>> =
        wrapResponse { api.callService(action.domain, action.service, action.allParams) }
}