package fr.outadoc.homeslide.app.onboarding.feature.host.model

data class ZeroconfHost(
    val hostName: String,
    val baseUrl: String?,
    val version: String?,
    val instanceName: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ZeroconfHost

        if (hostName != other.hostName) return false

        return true
    }

    override fun hashCode(): Int {
        return hostName.hashCode()
    }
}
