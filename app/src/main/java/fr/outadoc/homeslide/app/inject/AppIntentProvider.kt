package fr.outadoc.homeslide.app.inject

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import fr.outadoc.homeslide.app.controlprovider.inject.IntentProvider
import fr.outadoc.homeslide.app.feature.slideover.BarebonesMainActivity

class AppIntentProvider(private val context: Context) : IntentProvider {

    override fun getEntityDetailsActivityIntent(): PendingIntent {
        val i = Intent(context, BarebonesMainActivity::class.java)
        return PendingIntent.getActivity(context, CONTROL_REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        private const val CONTROL_REQUEST_CODE = 123
    }
}
