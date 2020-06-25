package fr.outadoc.homeslide.hassapi.model.discovery

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiStatus(
    @Json(name = "message")
    val message: String
)
