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

package fr.outadoc.homeslide.common.sync

import com.google.android.gms.wearable.DataEventBuffer
import fr.outadoc.homeslide.common.sync.model.DatabasePayload
import fr.outadoc.homeslide.common.sync.model.PreferencesPayload
import fr.outadoc.homeslide.logging.KLog

class NoopDataSyncClient : DataSyncClient {

    override fun syncDatabase(payload: DatabasePayload) {
        KLog.d { "syncDatabase($payload)" }
    }

    override fun syncPreferences(payload: PreferencesPayload) {
        KLog.d { "syncPreferences($payload)" }
    }

    override fun getPreferencesFromDataEvents(dataEvents: DataEventBuffer): PreferencesPayload? {
        KLog.d { "getPreferencesFromDataEvents" }
        return null
    }

    override fun getDatabaseFromDataEvents(dataEvents: DataEventBuffer): DatabasePayload? {
        KLog.d { "getDatabaseFromDataEvents" }
        return null
    }
}
