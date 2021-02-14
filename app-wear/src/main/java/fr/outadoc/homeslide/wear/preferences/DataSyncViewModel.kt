/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.outadoc.homeslide.wear.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.DataEventBuffer
import fr.outadoc.homeslide.common.persistence.EntityDao
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.common.sync.DataSyncClient
import fr.outadoc.homeslide.logging.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataSyncViewModel(
    private val dataSyncClient: DataSyncClient,
    private val globalPrefs: GlobalPreferenceRepository,
    private val urlPrefs: UrlPreferenceRepository,
    private val tokenPrefs: TokenPreferenceRepository,
    private val entityDao: EntityDao
) : ViewModel() {

    fun onSyncDataChanged(dataEvents: DataEventBuffer) {
        dataSyncClient.getPreferencesFromDataEvents(dataEvents)?.apply {
            KLog.d { "received prefs: $this" }

            urlPrefs.localInstanceBaseUrl = localInstanceBaseUrl
            urlPrefs.remoteInstanceBaseUrl = remoteInstanceBaseUrl
            tokenPrefs.accessToken = accessToken
            tokenPrefs.refreshToken = refreshToken
            globalPrefs.isOnboardingDone = true
        }

        dataSyncClient.getDatabaseFromDataEvents(dataEvents)?.apply {
            KLog.d { "received database update: $this" }

            viewModelScope.launch(Dispatchers.IO) {
                entityDao.replaceAll(entities)
            }
        }
    }
}
