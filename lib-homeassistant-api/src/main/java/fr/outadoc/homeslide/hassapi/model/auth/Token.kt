package fr.outadoc.homeslide.hassapi.model.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Token constructor(
    @Json(name = "access_token")
    val accessToken: String,
    @Json(name = "expires_in")
    val expiresIn: Long,
    @Json(name = "token_type")
    val tokenType: String,
    @Json(name = "refresh_token")
    val refreshToken: String? = null
)
