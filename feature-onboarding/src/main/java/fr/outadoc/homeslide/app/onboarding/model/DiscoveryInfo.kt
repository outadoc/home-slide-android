package fr.outadoc.homeslide.app.onboarding.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiscoveryInfo(
    val hostName: String? = null,
    @Json(name = "base_url")
    val baseUrl: String,
    @Json(name = "location_name")
    val locationName: String? = null,
    @Json(name = "requires_api_password")
    val requiresApiPassword: Boolean,
    @Json(name = "version")
    val version: String
)