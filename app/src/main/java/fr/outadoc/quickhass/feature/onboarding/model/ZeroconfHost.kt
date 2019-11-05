package fr.outadoc.quickhass.feature.onboarding.model

data class ZeroconfHost(
    val hostName: String,
    val baseUrl: String?,
    val version: String?,
    val instanceName: String?
)
