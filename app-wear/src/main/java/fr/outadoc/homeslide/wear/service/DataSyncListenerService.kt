package fr.outadoc.homeslide.wear.service

import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.wear.preferences.DataSyncViewModel
import org.koin.android.ext.android.inject

class DataSyncListenerService : WearableListenerService() {

    private val dataSyncViewModel: DataSyncViewModel by inject()

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        KLog.d { "receiving data from gms..." }
        dataSyncViewModel.onSyncDataChanged(dataEvents)
    }
}