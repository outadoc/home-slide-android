package fr.outadoc.quickhass.rest

import fr.outadoc.quickhass.model.DiscoveryInfo
import retrofit2.Response

interface InstanceRepository {
    suspend fun getDiscoveryInfo(): Response<DiscoveryInfo>
}