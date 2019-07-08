package fr.outadoc.quickhass.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.outadoc.quickhass.persistence.model.PersistedEntity

@Dao
interface EntityDao {

    @Query("SELECT * from persistedentity")
    fun getPersistedEntities(): List<PersistedEntity>

    @Insert
    fun insertAll(entities: List<PersistedEntity>)

    @Query("DELETE FROM persistedentity")
    fun deleteAllPersistedEntities()
}