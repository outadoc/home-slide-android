package fr.outadoc.homeslide.wear.service

import com.github.ajalt.timberkt.Timber
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService
import fr.outadoc.homeslide.wear.preferences.DataSyncViewModel
import org.koin.android.ext.android.inject

class DataSyncListenerService : WearableListenerService() {

    private val dataSyncViewModel: DataSyncViewModel by inject()

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Timber.d { "receiving data from gms..." }
        dataSyncViewModel.onSyncDataChanged(dataEvents)
    }
}