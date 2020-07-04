package fr.outadoc.homeslide.app.onboarding.feature.host.model

data class ZeroconfHost(
    val hostName: String,
    val baseUrl: String?,
    val version: String?,
    val instanceName: String?
)
