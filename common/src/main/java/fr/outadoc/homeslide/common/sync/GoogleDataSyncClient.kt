package fr.outadoc.homeslide.common.sync

import com.github.ajalt.timberkt.Timber
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.squareup.moshi.Moshi

class GoogleDataSyncClient(moshi: Moshi, private val dataClient: DataClient) :
    DataSyncClient {

    private val adapter = moshi.adapter(SyncPayload::class.java)

    companion object {
        private const val KEY_SYNC_PAYLOAD = "KEY_SYNC_PAYLOAD"
        private const val PATH_SYNC_PAYLOAD = "/payload"
    }

    override fun syncData(payload: SyncPayload) {
        val putDataReq = PutDataMapRequest.create(PATH_SYNC_PAYLOAD).run {
            dataMap.putString(KEY_SYNC_PAYLOAD, adapter.toJson(payload))
            setUrgent()
            asPutDataRequest()
        }

        Timber.d { "synchronizing prefs: $payload" }
        dataClient.putDataItem(putDataReq)
    }

    override fun getPayloadFromDataEvents(dataEvents: DataEventBuffer): SyncPayload? {
        return dataEvents.filter { event ->
            event.type != DataEvent.TYPE_DELETED
                && event.dataItem.uri.path == PATH_SYNC_PAYLOAD
        }.map { event ->
            val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
            val payloadStr = dataMap.getString(KEY_SYNC_PAYLOAD)
            adapter.fromJson(payloadStr)
        }.firstOrNull()
    }
}