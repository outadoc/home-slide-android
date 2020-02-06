package fr.outadoc.homeslide.hassapi.api.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import fr.outadoc.homeslide.hassapi.api.model.annotation.StringDomain
import fr.outadoc.homeslide.hassapi.api.model.annotation.StringEntityId
import fr.outadoc.homeslide.hassapi.api.model.annotation.StringState
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

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