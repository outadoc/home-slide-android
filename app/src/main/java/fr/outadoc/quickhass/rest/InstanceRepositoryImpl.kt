package fr.outadoc.quickhass.rest

import fr.outadoc.quickhass.model.DiscoveryInfo
import fr.outadoc.quickhass.preferences.PreferenceRepository

class InstanceRepositoryImpl(prefs: PreferenceRepository) :
    BaseApiRepository<HomeAssistantApi>(HomeAssistantApi::class.java, prefs),
    InstanceRepository {

    override suspend fun getDiscoveryInfo(): Result<DiscoveryInfo> =
        wrapResponse { api.getDiscoveryInfo() }
}