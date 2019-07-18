package fr.outadoc.quickhass.feature.slideover.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import fr.outadoc.quickhass.feature.slideover.model.annotation.StringDomain
import fr.outadoc.quickhass.feature.slideover.model.annotation.StringEntityId
import fr.outadoc.quickhass.feature.slideover.model.annotation.StringState

@JsonClass(generateAdapter = true)
data class State(
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
) {
    @StringDomain
    val domain: String = entityId.takeWhile { it != '.' }
}