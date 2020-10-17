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

package fr.outadoc.homeslide.app.feature.review

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class InAppReviewLaunchCounter(context: Context) {

    companion object {
        private const val TRIGGER_THRESHOLD = 30
        private const val KEY_LAUNCH_COUNT = "pref_review_launch_count"
    }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val lock = Any()

    /**
     * Increments the counter.
     * @return true if the threshold was reached.
     */
    fun increment(): Boolean = synchronized(lock) {
        val count = sharedPreferences.getInt(KEY_LAUNCH_COUNT, 0)
        val newCount = (count + 1) % TRIGGER_THRESHOLD
        sharedPreferences.edit {
            putInt(KEY_LAUNCH_COUNT, newCount)
        }
        count == TRIGGER_THRESHOLD - 1
    }
}
