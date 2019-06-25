package fr.outadoc.quickhass.rest

import fr.outadoc.quickhass.model.Action

class HomeAssistantServer : BaseServer<HomeAssistantApi>(HomeAssistantApi::class.java) {

    suspend fun getStates() = api.getStates()

    suspend fun getServices() = api.getServices()

    suspend fun callService(action: Action) =
        api.callService(action.domain, action.service, action.allParams)
}