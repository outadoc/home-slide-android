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

package fr.outadoc.homeslide.app.inject

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import fr.outadoc.homeslide.app.controlprovider.inject.IntentProvider
import fr.outadoc.homeslide.app.feature.slideover.BarebonesMainActivity

class AppIntentProvider : IntentProvider {

    override fun getEntityDetailsActivityIntent(context: Context): PendingIntent {
        val i = Intent(context, BarebonesMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(context, CONTROL_REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        private const val CONTROL_REQUEST_CODE = 0
    }
}
