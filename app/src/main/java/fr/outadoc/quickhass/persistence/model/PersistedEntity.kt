package fr.outadoc.quickhass.persistence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PersistedEntity(
    @PrimaryKey
    val entityId: String,
    @ColumnInfo(name = "order")
    val order: Int
)