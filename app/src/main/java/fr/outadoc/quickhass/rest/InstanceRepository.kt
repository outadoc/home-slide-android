package fr.outadoc.quickhass.rest

import fr.outadoc.quickhass.model.DiscoveryInfo

interface InstanceRepository {
    suspend fun getDiscoveryInfo(): Result<DiscoveryInfo>
}