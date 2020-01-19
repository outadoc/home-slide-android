package fr.outadoc.quickhass.feature.slideover

import fr.outadoc.quickhass.feature.slideover.model.Tile
import fr.outadoc.quickhass.model.entity.Entity

class TileFactoryImpl : TileFactory {

    override fun create(entity: Entity): Tile<Entity> {
        return Tile(
            source = entity,
            isActivated = entity.isOn,
            label = entity.friendlyName ?: entity.entityId,
            state = entity.formattedState,
            icon = if (entity.formattedState == null) {
                entity.icon.unicodePoint
            } else {
                null
            },
            isLoading = false,
            isHidden = false
        )
    }
}