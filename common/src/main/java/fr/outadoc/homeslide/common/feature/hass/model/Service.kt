package fr.outadoc.homeslide.common.feature.hass.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import fr.outadoc.homeslide.common.feature.hass.model.annotation.StringDomain

@JsonClass(generateAdapter = true)
data class Service(
    @Json(name = "domain")
    @StringDomain
    val domain: String,
    @Json(name = "services")
    val services: List<String>
)