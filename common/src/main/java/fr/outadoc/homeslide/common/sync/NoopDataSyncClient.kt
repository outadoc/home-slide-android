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