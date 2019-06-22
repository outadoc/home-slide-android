package fr.outadoc.quickhass.shortcuts

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class QuickAccessTileService : TileService() {

    override fun onClick() {
        super.onClick()

        val intent = Intent(this, QuickAccessActivity::class.java)
            .addFlags(FLAG_ACTIVITY_NEW_TASK)

        startActivityAndCollapse(intent)
    }

    override fun onStartListening() {
        with(qsTile) {
            state = Tile.STATE_INACTIVE
            updateTile()
        }
    }
}