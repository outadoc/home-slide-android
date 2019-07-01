package fr.outadoc.quickhass.service

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import fr.outadoc.quickhass.slideover.SlideOverActivity

class QuickAccessTileService : TileService() {

    override fun onClick() {
        super.onClick()

        val intent = Intent(this, SlideOverActivity::class.java)
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