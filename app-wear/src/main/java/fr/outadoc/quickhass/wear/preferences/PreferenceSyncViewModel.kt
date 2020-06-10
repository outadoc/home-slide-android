package fr.outadoc.quickhass.wear.preferences

import androidx.lifecycle.ViewModel
import com.github.ajalt.timberkt.Timber
import com.google.android.gms.wearable.DataEventBuffer
import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.common.sync.DataSyncClient

class PreferenceSyncViewModel(
    private val dataSyncClient: DataSyncClient,
    private val urlPrefs: UrlPreferenceRepository,
    private val tokenPrefs: TokenPreferenceRepository
) : ViewModel() {

    fun onSyncDataChanged(dataEvents: DataEventBuffer) {
        dataSyncClient.getPayloadFromDataEvents(dataEvents)?.apply {
            Timber.d { "received prefs: $this" }

            urlPrefs.instanceBaseUrl = instanceBaseUrl
            urlPrefs.altInstanceBaseUrl = altInstanceBaseUrl
            tokenPrefs.accessToken = accessToken
            tokenPrefs.refreshToken = refreshToken
        }
    }
}