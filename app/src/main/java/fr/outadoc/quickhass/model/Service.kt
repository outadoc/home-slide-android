package fr.outadoc.quickhass.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import fr.outadoc.quickhass.model.annotation.StringDomain

@JsonClass(generateAdapter = true)
data class Service(
    @Json(name = "domain")
    @StringDomain
    val domain: String,
    @Json(name = "services")
    val services: List<String>
)