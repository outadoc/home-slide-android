package fr.outadoc.homeslide.wear.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.DataEventBuffer
import fr.outadoc.homeslide.common.persistence.EntityDao
import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.common.sync.DataSyncClient
import fr.outadoc.homeslide.logging.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataSyncViewModel(
    private val dataSyncClient: DataSyncClient,
    private val urlPrefs: UrlPreferenceRepository,
    private val tokenPrefs: TokenPreferenceRepository,
    private val entityDao: EntityDao
) : ViewModel() {

    fun onSyncDataChanged(dataEvents: DataEventBuffer) {
        dataSyncClient.getPreferencesFromDataEvents(dataEvents)?.apply {
            KLog.d { "received prefs: $this" }

            urlPrefs.instanceBaseUrl = instanceBaseUrl
            urlPrefs.altInstanceBaseUrl = altInstanceBaseUrl
            tokenPrefs.accessToken = accessToken
            tokenPrefs.refreshToken = refreshToken
        }

        dataSyncClient.getDatabaseFromDataEvents(dataEvents)?.apply {
            KLog.d { "received database update: $this" }

            viewModelScope.launch(Dispatchers.IO) {
                entityDao.replaceAll(entities)
            }
        }
    }
}