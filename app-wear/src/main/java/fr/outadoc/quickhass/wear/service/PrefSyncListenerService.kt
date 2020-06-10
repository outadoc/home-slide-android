package fr.outadoc.quickhass.wear.service

import com.github.ajalt.timberkt.Timber
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService
import fr.outadoc.quickhass.wear.preferences.PreferenceSyncViewModel
import org.koin.android.ext.android.inject

class PrefSyncListenerService : WearableListenerService() {

    private val preferenceSyncViewModel: PreferenceSyncViewModel by inject()

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Timber.d { "receiving data from gms..." }
        preferenceSyncViewModel.onSyncDataChanged(dataEvents)
    }
}