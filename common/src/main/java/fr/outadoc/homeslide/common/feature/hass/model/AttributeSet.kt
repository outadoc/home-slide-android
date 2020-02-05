package fr.outadoc.homeslide.common.feature.hass.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import fr.outadoc.mdi.IconStringRef
import kotlinx.android.parcel.Parcelize

@Parcelize
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
    val operationList: List<String>?,
    @Json(name = "current_temperature")
    val currentTemperature: Float? = null,
    @Json(name = "unit_of_measurement")
    val unit: String? = null,
    @Json(name = "brightness")
    val brightness: Float? = null
) : Parcelable
