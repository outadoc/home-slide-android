package fr.outadoc.homeslide.common.sync

import com.google.android.gms.wearable.DataEventBuffer
import fr.outadoc.homeslide.common.sync.model.DatabasePayload
import fr.outadoc.homeslide.common.sync.model.PreferencesPayload

interface DataSyncClient {

    fun syncDatabase(payload: DatabasePayload)
    fun syncPreferences(payload: PreferencesPayload)
    fun getPreferencesFromDataEvents(dataEvents: DataEventBuffer): PreferencesPayload?
    fun getDatabaseFromDataEvents(dataEvents: DataEventBuffer): DatabasePayload?
}