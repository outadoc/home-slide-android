package fr.outadoc.quickhass.rest

import fr.outadoc.quickhass.model.DiscoveryInfo
import fr.outadoc.quickhass.preferences.PreferenceRepository

class InstanceRepositoryImpl(prefs: PreferenceRepository) : InstanceRepository {

    private val client = RestClient.create<HomeAssistantApi>(prefs)

    override suspend fun getDiscoveryInfo(): Result<DiscoveryInfo> =
        wrapResponse { client.getDiscoveryInfo() }
}