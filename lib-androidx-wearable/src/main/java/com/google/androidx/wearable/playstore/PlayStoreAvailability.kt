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

package com.google.androidx.wearable.playstore

import android.content.Context
import android.net.Uri

object PlayStoreAvailability {

    const val PLAY_STORE_ON_PHONE_ERROR_UNKNOWN = 0
    const val PLAY_STORE_ON_PHONE_AVAILABLE = 1
    const val PLAY_STORE_ON_PHONE_UNAVAILABLE = 2

    private const val KEY_PLAY_STORE_AVAILABILITY = "play_store_availability"
    private const val PLAY_STORE_AVAILABILITY_PATH = "play_store_availability"

    private val PLAY_STORE_AVAILABILITY_URI =
        Uri.Builder()
            .scheme("content")
            .authority("com.google.android.wearable.settings")
            .path(PLAY_STORE_AVAILABILITY_PATH)
            .build()

    fun getPlayStoreAvailabilityOnPhone(context: Context): Int {
        val cursor = context.contentResolver.query(
            PLAY_STORE_AVAILABILITY_URI, null, null, null, null
        )

        return cursor?.use { c ->
            do {
                if (!c.moveToNext()) {
                    return 0
                }
            } while (KEY_PLAY_STORE_AVAILABILITY != c.getString(0))
            c.getInt(1)
        } ?: 0
    }
}
