package fr.outadoc.homeslide.app.controlprovider.inject

import android.app.PendingIntent

interface DetailsIntentProvider {
    fun getEntityDetailsActivityIntent(): PendingIntent
}
