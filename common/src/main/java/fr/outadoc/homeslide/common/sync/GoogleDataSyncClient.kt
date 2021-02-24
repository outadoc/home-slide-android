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

import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import fr.outadoc.homeslide.common.sync.model.DatabasePayload
import fr.outadoc.homeslide.common.sync.model.PreferencesPayload
import fr.outadoc.homeslide.logging.KLog
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GoogleDataSyncClient(private val json: Json, private val dataClient: DataClient) : DataSyncClient {

    companion object {
        private const val PATH_SYNC_PREFERENCES = "/sync-preferences"
        private const val PATH_SYNC_DATABASE = "/sync-database"

        private const val KEY_PREFERENCES_PAYLOAD = "KEY_PREFERENCES_PAYLOAD"
        private const val KEY_DATABASE_PAYLOAD = "KEY_DATABASE_PAYLOAD"
    }

    override fun syncPreferences(payload: PreferencesPayload) {
        KLog.d { "synchronizing prefs: $payload" }

        try {
            val putDataReq = PutDataMapRequest.create(PATH_SYNC_PREFERENCES).run {
                dataMap.putString(KEY_PREFERENCES_PAYLOAD, json.encodeToString(payload))
                setUrgent()
                asPutDataRequest()
            }

            dataClient.putDataItem(putDataReq)
        } catch (e: Exception) {
            KLog.e(e)
        }
    }

    override fun syncDatabase(payload: DatabasePayload) {
        KLog.d { "synchronizing database: $payload" }

        try {
            val putDataReq = PutDataMapRequest.create(PATH_SYNC_DATABASE).run {
                dataMap.putString(KEY_DATABASE_PAYLOAD, json.encodeToString(payload))
                asPutDataRequest()
            }

            dataClient.putDataItem(putDataReq)
        } catch (e: Exception) {
            KLog.e(e)
        }
    }

    override fun getPreferencesFromDataEvents(dataEvents: DataEventBuffer): PreferencesPayload? {
        return dataEvents.filter { event ->
            event.type != DataEvent.TYPE_DELETED &&
                event.dataItem.uri.path == PATH_SYNC_PREFERENCES
        }.map { event ->
            val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
            val payloadStr = dataMap.getString(KEY_PREFERENCES_PAYLOAD)
            json.decodeFromString<PreferencesPayload?>(payloadStr)
        }.firstOrNull()
    }

    override fun getDatabaseFromDataEvents(dataEvents: DataEventBuffer): DatabasePayload? {
        return dataEvents.filter { event ->
            event.type != DataEvent.TYPE_DELETED &&
                event.dataItem.uri.path == PATH_SYNC_DATABASE
        }.map { event ->
            val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
            val payloadStr = dataMap.getString(KEY_DATABASE_PAYLOAD)
            json.decodeFromString<DatabasePayload?>(payloadStr)
        }.firstOrNull()
    }
}
