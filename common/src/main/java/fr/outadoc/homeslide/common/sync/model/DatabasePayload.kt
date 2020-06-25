package fr.outadoc.homeslide.common.sync.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import fr.outadoc.homeslide.hassapi.model.PersistedEntity

@JsonClass(generateAdapter = true)
data class DatabasePayload(
    @Json(name = "entities")
    val entities: List<PersistedEntity>
)
