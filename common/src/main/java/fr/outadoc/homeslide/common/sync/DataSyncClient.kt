package fr.outadoc.homeslide.common.sync

import com.google.android.gms.wearable.DataEventBuffer
import fr.outadoc.homeslide.common.sync.SyncPayload

interface DataSyncClient {

    fun syncData(payload: SyncPayload)
    fun getPayloadFromDataEvents(dataEvents: DataEventBuffer): SyncPayload?
}