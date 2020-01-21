package fr.outadoc.quickhass.feature.slideover

import android.content.Context
import fr.outadoc.quickhass.feature.slideover.model.Tile
import fr.outadoc.quickhass.model.entity.Entity

class TileFactoryImpl(private val context: Context) : TileFactory {

    override fun create(entity: Entity): Tile<Entity> {
        val state = entity.getFormattedState(context)
        return Tile(
            source = entity,
            isActivated = entity.isOn,
            label = entity.friendlyName ?: entity.entityId,
            state = state,
            icon = if (state == null) entity.icon.unicodePoint else null,
            isLoading = false,
            isHidden = false
        )
    }
}