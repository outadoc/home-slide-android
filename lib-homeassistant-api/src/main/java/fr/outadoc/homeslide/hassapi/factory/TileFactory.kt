package fr.outadoc.homeslide.hassapi.factory

import fr.outadoc.homeslide.hassapi.model.Tile
import fr.outadoc.homeslide.hassapi.model.entity.Entity

interface TileFactory {
    fun create(entity: Entity): Tile<Entity>
}
