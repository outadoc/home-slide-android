package fr.outadoc.homeslide.hassapi.factory

import fr.outadoc.homeslide.hassapi.api.model.entity.Entity
import fr.outadoc.homeslide.hassapi.model.Tile

interface TileFactory {
    fun create(entity: Entity): Tile<Entity>
}