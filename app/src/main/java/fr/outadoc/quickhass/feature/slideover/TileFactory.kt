package fr.outadoc.quickhass.feature.slideover

import fr.outadoc.quickhass.feature.slideover.model.Tile
import fr.outadoc.quickhass.model.entity.Entity

interface TileFactory {
    fun create(entity: Entity): Tile<Entity>
}