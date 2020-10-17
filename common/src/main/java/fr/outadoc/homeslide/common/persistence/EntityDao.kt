/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
