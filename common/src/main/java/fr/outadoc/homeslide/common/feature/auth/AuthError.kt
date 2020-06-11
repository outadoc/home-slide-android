package fr.outadoc.homeslide.common.feature.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthError(
    @Json(name = "error")
    val errorCode: String?,
    @Json(name = "error_description")
    val description: String?
)