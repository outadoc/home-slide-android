package fr.outadoc.quickhass.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.outadoc.quickhass.persistence.dao.EntityDao
import fr.outadoc.quickhass.persistence.model.PersistedEntity

@Database(entities = [PersistedEntity::class], version = 1)
abstract class EntityDatabase : RoomDatabase() {
    abstract fun entityDao(): EntityDao

    companion object {
        const val DB_NAME = "quickhass.db"
    }
}