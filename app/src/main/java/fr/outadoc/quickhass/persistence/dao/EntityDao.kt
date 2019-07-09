package fr.outadoc.quickhass.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import fr.outadoc.quickhass.persistence.model.PersistedEntity

@Dao
interface EntityDao {

    @Query("SELECT * from persistedentity")
    fun getPersistedEntities(): List<PersistedEntity>

    @Insert
    fun insertAll(entities: List<PersistedEntity>)

    @Query("DELETE FROM persistedentity")
    fun deleteAllPersistedEntities()

    @Transaction
    fun replaceAll(entities: List<PersistedEntity>) {
        deleteAllPersistedEntities()
        insertAll(entities)
    }
}