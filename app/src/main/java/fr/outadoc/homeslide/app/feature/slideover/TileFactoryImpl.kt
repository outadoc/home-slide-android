package fr.outadoc.homeslide.app.feature.slideover

import android.content.Context
import fr.outadoc.homeslide.common.feature.hass.model.Tile
import fr.outadoc.homeslide.common.feature.hass.model.entity.Entity

class TileFactoryImpl(private val context: Context) : TileFactory {

    override fun create(entity: Entity): Tile<Entity> {
        val state = entity.getFormattedState(context)
        return Tile(
            source = entity,
            isActivated = entity.isOn,
            isToggleable = entity.isToggleable,
            label = entity.friendlyName ?: entity.entityId,
            state = state,
            icon = if (state == null) entity.icon.unicodePoint else null,
            isLoading = false,
            isHidden = false
        )
    }
}