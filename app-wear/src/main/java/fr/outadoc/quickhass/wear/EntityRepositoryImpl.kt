package fr.outadoc.quickhass.wear

import fr.outadoc.homeslide.hassapi.api.HomeAssistantApi
import fr.outadoc.homeslide.hassapi.factory.EntityFactory
import fr.outadoc.homeslide.hassapi.factory.TileFactory
import fr.outadoc.homeslide.hassapi.model.Action
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.PersistedEntity
import fr.outadoc.homeslide.hassapi.model.Tile
import fr.outadoc.homeslide.hassapi.model.entity.Cover
import fr.outadoc.homeslide.hassapi.model.entity.Entity
import fr.outadoc.homeslide.hassapi.model.entity.Light
import fr.outadoc.homeslide.hassapi.model.entity.Weather
import fr.outadoc.homeslide.hassapi.repository.EntityRepository
import fr.outadoc.homeslide.rest.util.wrapResponse

class EntityRepositoryImpl(
    private val tileFactory: TileFactory,
    private val client: HomeAssistantApi
) : EntityRepository {

    override suspend fun getEntityTiles(): Result<List<Tile<Entity>>> {
        return wrapResponse { client.getStates() }
            .map { states ->
                states.asSequence()
                    .map { EntityFactory.create(it) }
                    .map { entity ->
                        tileFactory
                            .create(entity)
                            .copy(
                                isHidden = !entity.isVisible || INITIAL_DOMAIN_BLACKLIST.contains(
                                    entity.domain
                                )
                            )
                    }
                    .sortedWith(
                        compareBy(
                            // Shove hidden tiles to the end of the list initially
                            { tile -> tile.isHidden },

                            // Order by domain priority (put lights and covers first for example)
                            { tile ->
                                getPriorityForDomain(tile.source.domain) ?: Int.MAX_VALUE
                            },

                            // Order by domain so that the items are somewhat sorted
                            { tile -> tile.source.domain },

                            // Order by label within a domain
                            { tile -> tile.source.friendlyName }
                        )
                    )
                    .toList()
            }
    }

    override suspend fun callService(action: Action): Result<List<EntityState>> =
        wrapResponse {
            client.callService(
                action.domain,
                action.service,
                action.allParams
            )
        }

    override suspend fun saveEntityListState(entities: List<PersistedEntity>) {}

    private fun getPriorityForDomain(domain: String): Int? {
        return when (domain) {
            Light.DOMAIN -> 0
            Cover.DOMAIN -> 1
            Weather.DOMAIN -> 2
            else -> null
        }
    }

    companion object {
        val INITIAL_DOMAIN_BLACKLIST = listOf(
            "automation",
            "device_tracker",
            "updater",
            "camera",
            "persistent_notification"
        )
    }
}