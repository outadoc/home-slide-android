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

package fr.outadoc.homeslide.app.controlprovider.repository

import android.content.Context
import android.os.Build
import android.service.controls.Control
import androidx.annotation.RequiresApi
import fr.outadoc.homeslide.hassapi.model.annotation.StringEntityId

@RequiresApi(Build.VERSION_CODES.R)
interface ControlRepository {
    suspend fun getEntities(context: Context): List<Control>
    suspend fun getEntitiesWithState(context: Context): List<Control>
    suspend fun toggleEntity(@StringEntityId entityId: String)
}
