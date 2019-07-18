package fr.outadoc.quickhass.feature.slideover.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import fr.outadoc.mdi.IconStringRef

@JsonClass(generateAdapter = true)
data class AttributeSet(
    @Json(name = "friendly_name")
    val friendlyName: String?,
    @IconStringRef
    @Json(name = "icon")
    val icon: String?,
    @Json(name = "hidden")
    val isHidden: Boolean = false,
    @Json(name = "operation_list")
    val operationList: List<String>?
)
