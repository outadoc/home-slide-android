package fr.outadoc.quickhass.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttributeSet(
    @Json(name = "friendly_name")
    val friendlyName: String?,
    @Json(name = "icon")
    val icon: String?,
    @Json(name = "hidden")
    val hidden: Boolean = false
)
