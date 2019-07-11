package fr.outadoc.quickhass.rest

import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.preferences.PreferenceRepository

class EntityRepositoryImpl(prefs: PreferenceRepository) :
    BaseServer<HomeAssistantApi>(HomeAssistantApi::class.java, prefs),
    EntityRepository {

    override suspend fun getStates() = api.getStates()

    override suspend fun getServices() = api.getServices()

    override suspend fun callService(action: Action) =
        api.callService(action.domain, action.service, action.allParams)
}