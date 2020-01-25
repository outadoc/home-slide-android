package fr.outadoc.quickhass.service

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import fr.outadoc.quickhass.feature.slideover.SlideOverActivity

@RequiresApi(Build.VERSION_CODES.N)
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