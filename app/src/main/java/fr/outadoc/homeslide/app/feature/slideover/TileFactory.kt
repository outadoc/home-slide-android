package fr.outadoc.homeslide.app.feature.slideover

import fr.outadoc.homeslide.common.feature.hass.model.Tile
import fr.outadoc.homeslide.common.feature.hass.model.entity.Entity

interface TileFactory {
    fun create(entity: Entity): Tile<Entity>
}