package fr.outadoc.homeslide.common.sync

import com.github.ajalt.timberkt.Timber
import com.google.android.gms.wearable.DataEventBuffer
import fr.outadoc.homeslide.common.sync.model.DatabasePayload
import fr.outadoc.homeslide.common.sync.model.PreferencesPayload

class NoopDataSyncClient : DataSyncClient {

    override fun syncDatabase(payload: DatabasePayload) {
        Timber.d { "syncDatabase($payload)" }
    }

    override fun syncPreferences(payload: PreferencesPayload) {
        Timber.d { "syncPreferences($payload)" }
    }

    override fun getPreferencesFromDataEvents(dataEvents: DataEventBuffer): PreferencesPayload? {
        Timber.d { "getPreferencesFromDataEvents" }
        return null
    }

    override fun getDatabaseFromDataEvents(dataEvents: DataEventBuffer): DatabasePayload? {
        Timber.d { "getDatabaseFromDataEvents" }
        return null
    }
}