package fr.outadoc.quickhass.onboarding.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiscoveryInfo(
        @Json(name = "base_url")
    val baseUrl: String,
        @Json(name = "location_name")
    val locationName: String,
        @Json(name = "requires_api_password")
        val requiresApiPassword: Boolean,
        @Json(name = "version")
    val version: String
)