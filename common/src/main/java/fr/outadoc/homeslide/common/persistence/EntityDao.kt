package fr.outadoc.homeslide.common.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import fr.outadoc.homeslide.hassapi.model.PersistedEntity

@Dao
interface EntityDao {

    @Query("""SELECT * from PersistedEntity ORDER BY hidden, "order"""")
    suspend fun getPersistedEntities(): List<PersistedEntity>

    @Insert
    suspend fun insertAll(entities: List<PersistedEntity>)

    @Query("DELETE FROM PersistedEntity")
    suspend fun deleteAllPersistedEntities()

    @Transaction
    suspend fun replaceAll(entities: List<PersistedEntity>) {
        deleteAllPersistedEntities()
        insertAll(entities)
    }
}
