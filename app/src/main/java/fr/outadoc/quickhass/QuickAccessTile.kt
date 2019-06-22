package fr.outadoc.quickhass

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.service.quicksettings.TileService

class QuickAccessTile : TileService() {

    override fun onClick() {
        super.onClick()

        val intent = Intent(this, QuickAccessActivity::class.java)
            .addFlags(FLAG_ACTIVITY_NEW_TASK)

        startActivityAndCollapse(intent)
    }
}