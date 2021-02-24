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
import fr.outadoc.mdi.common.MdiStringRef
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class AttributeSet(
    @SerialName("friendly_name")
    val friendlyName: String?,
    @MdiStringRef
    @SerialName("icon")
    val icon: String?,
    @SerialName("hidden")
    val isHidden: Boolean = false,
    @SerialName("operation_list")
    val operationList: List<String>?,
    @SerialName("current_temperature")
    val currentTemperature: Float? = null,
    @SerialName("unit_of_measurement")
    val unit: String? = null,
    @SerialName("brightness")
    val brightness: Float? = null,
    @SerialName("min")
    val min: Float? = null,
    @SerialName("max")
    val max: Float? = null
) : Parcelable
