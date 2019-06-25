package fr.outadoc.quickhass.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import fr.outadoc.quickhass.model.annotation.StringIcon

@JsonClass(generateAdapter = true)
data class AttributeSet(
    @Json(name = "friendly_name")
    val friendlyName: String?,
    @Json(name = "icon")
    @StringIcon
    val icon: String?,
    @Json(name = "hidden")
    val hidden: Boolean = false
)
