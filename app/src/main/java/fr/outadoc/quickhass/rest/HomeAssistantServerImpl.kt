package fr.outadoc.quickhass.rest

import fr.outadoc.quickhass.model.Action

class HomeAssistantServerImpl : BaseServer<HomeAssistantApi>(HomeAssistantApi::class.java),
    HomeAssistantServer {

    override suspend fun getStates() = api.getStates()

    override suspend fun getServices() = api.getServices()

    override suspend fun callService(action: Action) =
        api.callService(action.domain, action.service, action.allParams)
}