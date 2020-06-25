package fr.outadoc.homeslide.app.controlprovider.inject

import android.app.PendingIntent

interface IntentProvider {
    fun getEntityDetailsActivityIntent(): PendingIntent
}
