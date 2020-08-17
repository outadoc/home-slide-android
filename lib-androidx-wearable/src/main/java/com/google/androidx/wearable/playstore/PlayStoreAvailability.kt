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
