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
import fr.outadoc.homeslide.hassapi.model.annotation.StringDomain
import fr.outadoc.homeslide.hassapi.model.annotation.StringEntityId
import fr.outadoc.homeslide.hassapi.model.annotation.StringState
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class EntityState(
    @Json(name = "entity_id")
    @StringEntityId
    val entityId: String,
    @Json(name = "last_changed")
    val lastChanged: String,
    @Json(name = "last_updated")
    val lastUpdated: String,
    @Json(name = "state")
    @StringState
    val state: String,
    @Json(name = "attributes")
    val attributes: AttributeSet
) : Parcelable {

    @IgnoredOnParcel
    @StringDomain
    val domain: String = entityId.takeWhile { it != '.' }
}
