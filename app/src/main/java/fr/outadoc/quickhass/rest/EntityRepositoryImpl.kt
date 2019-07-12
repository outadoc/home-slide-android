package fr.outadoc.quickhass.rest

import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.model.Service
import fr.outadoc.quickhass.model.State
import fr.outadoc.quickhass.preferences.PreferenceRepository
import retrofit2.Response

class EntityRepositoryImpl(prefs: PreferenceRepository) :
    BaseApiRepository<HomeAssistantApi>(HomeAssistantApi::class.java, prefs),
    EntityRepository {

    override suspend fun getStates(): Response<List<State>> =
        api.getStates()

    override suspend fun getServices(): Response<List<Service>> =
        api.getServices()

    override suspend fun callService(action: Action): Response<List<State>> =
        api.callService(action.domain, action.service, action.allParams)
}