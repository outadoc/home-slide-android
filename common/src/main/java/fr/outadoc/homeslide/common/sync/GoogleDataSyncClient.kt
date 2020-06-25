package fr.outadoc.homeslide.common.sync

import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.squareup.moshi.Moshi
import fr.outadoc.homeslide.common.sync.model.DatabasePayload
import fr.outadoc.homeslide.common.sync.model.PreferencesPayload
import fr.outadoc.homeslide.logging.KLog

class GoogleDataSyncClient(moshi: Moshi, private val dataClient: DataClient) : DataSyncClient {

    companion object {
        private const val PATH_SYNC_PREFERENCES = "/sync-preferences"
        private const val PATH_SYNC_DATABASE = "/sync-database"

        private const val KEY_PREFERENCES_PAYLOAD = "KEY_PREFERENCES_PAYLOAD"
        private const val KEY_DATABASE_PAYLOAD = "KEY_DATABASE_PAYLOAD"
    }

    private val prefsAdapter = moshi.adapter(PreferencesPayload::class.java)
    private val dbAdapter = moshi.adapter(DatabasePayload::class.java)

    override fun syncPreferences(payload: PreferencesPayload) {
        KLog.d { "synchronizing prefs: $payload" }

        try {
            val putDataReq = PutDataMapRequest.create(PATH_SYNC_PREFERENCES).run {
                dataMap.putString(KEY_PREFERENCES_PAYLOAD, prefsAdapter.toJson(payload))
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
                dataMap.putString(KEY_DATABASE_PAYLOAD, dbAdapter.toJson(payload))
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
            prefsAdapter.fromJson(payloadStr)
        }.firstOrNull()
    }

    override fun getDatabaseFromDataEvents(dataEvents: DataEventBuffer): DatabasePayload? {
        return dataEvents.filter { event ->
            event.type != DataEvent.TYPE_DELETED &&
                event.dataItem.uri.path == PATH_SYNC_DATABASE
        }.map { event ->
            val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
            val payloadStr = dataMap.getString(KEY_DATABASE_PAYLOAD)
            dbAdapter.fromJson(payloadStr)
        }.firstOrNull()
    }
}
