package fr.outadoc.quickhass.rest

class HomeAssistantServer : BaseServer<HomeAssistantApi>(HomeAssistantApi::class.java) {

    suspend fun getStates() = api.getStates()
    suspend fun getServices() = api.getServices()
    suspend fun callService(domain: String, service: String) = api.callService(domain, service)
}