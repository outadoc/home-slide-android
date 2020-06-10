package fr.outadoc.homeslide.hassapi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class PersistedEntity(
    @PrimaryKey
    @Json(name = "i")
    val entityId: String,
    @ColumnInfo(name = "order")
    @Json(name = "o")
    val order: Int,
    @ColumnInfo(name = "hidden", defaultValue = "0")
    @Json(name = "h")
    val hidden: Boolean
)