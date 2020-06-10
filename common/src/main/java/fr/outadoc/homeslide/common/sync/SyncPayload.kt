package fr.outadoc.homeslide.common.sync

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SyncPayload(
    @Json(name = "instanceBaseUrl")
    val instanceBaseUrl: String?,
    @Json(name = "altInstanceBaseUrl")
    val altInstanceBaseUrl: String?,
    @Json(name = "accessToken")
    val accessToken: String?,
    @Json(name = "refreshToken")
    val refreshToken: String?
)