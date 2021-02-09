/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.outadoc.homeslide.hassapi.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import fr.outadoc.mdi.common.MdiStringRef
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class AttributeSet(
    @Json(name = "friendly_name")
    val friendlyName: String?,
    @MdiStringRef
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
    val brightness: Float? = null,
    @Json(name = "min")
    val min: Float? = null,
    @Json(name = "max")
    val max: Float? = null
) : Parcelable
