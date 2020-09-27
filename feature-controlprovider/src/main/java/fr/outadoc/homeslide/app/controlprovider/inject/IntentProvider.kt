package fr.outadoc.homeslide.app.controlprovider.inject

import android.app.PendingIntent
import android.content.Context

interface IntentProvider {
    fun getEntityDetailsActivityIntent(context: Context): PendingIntent
}
