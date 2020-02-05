package fr.outadoc.homeslide.app.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.outadoc.homeslide.app.persistence.dao.EntityDao
import fr.outadoc.homeslide.app.persistence.model.PersistedEntity

@Database(entities = [PersistedEntity::class], version = 2)
abstract class EntityDatabase : RoomDatabase() {
    abstract fun entityDao(): EntityDao

    companion object {
        const val DB_NAME = "quickhass.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE PersistedEntity ADD COLUMN hidden INTEGER DEFAULT 0 NOT NULL")
            }
        }
    }
}