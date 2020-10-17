/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.outadoc.homeslide.common.feature.slideover

import fr.outadoc.homeslide.common.persistence.EntityDao
import fr.outadoc.homeslide.common.sync.DataSyncClient
import fr.outadoc.homeslide.common.sync.model.DatabasePayload
import fr.outadoc.homeslide.hassapi.api.HomeAssistantApi
import fr.outadoc.homeslide.hassapi.factory.EntityFactory
import fr.outadoc.homeslide.hassapi.factory.TileFactory
import fr.outadoc.homeslide.hassapi.model.Action
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.PersistedEntity
import fr.outadoc.homeslide.hassapi.model.Tile
import fr.outadoc.homeslide.hassapi.model.entity.Cover
import fr.outadoc.homeslide.hassapi.model.entity.Light
import fr.outadoc.homeslide.hassapi.model.entity.Weather
import fr.outadoc.homeslide.hassapi.model.entity.base.Entity
import fr.outadoc.homeslide.hassapi.repository.EntityRepository
import fr.outadoc.homeslide.rest.util.getResponseOrThrow

class EntityRepositoryImpl(
    private val dao: EntityDao,
    private val tileFactory: TileFactory,
    private val client: HomeAssistantApi,
    private val syncClient: DataSyncClient
) : EntityRepository {

    override suspend fun getEntityTiles(): List<Tile<Entity>> {
        val persistedEntities = dao.getPersistedEntities()
            .map { it.entityId to it }
            .toMap()

        return client.getStates()
            .getResponseOrThrow()
            .asSequence()
            .map { EntityFactory.create(it) }
            .map { entity ->
                val persistedEntity = persistedEntities[entity.entityId]
                tileFactory
                    .create(entity)
                    .copy(
                        isHidden = persistedEntity?.hidden
                            ?: !entity.isVisible || INITIAL_DOMAIN_BLACKLIST.contains(entity.domain)
                    )
            }
            .sortedWith(
                compareBy(
                    // If the user has already ordered the item manually, use that order
                    // Otherwise put it at the end of the list initially
                    { tile -> persistedEntities[tile.source.entityId]?.order ?: Int.MAX_VALUE },

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

    override suspend fun saveEntityListState(entities: List<PersistedEntity>) {
        dao.replaceAll(entities)
        syncClient.syncDatabase(DatabasePayload(entities))
    }

    private fun getPriorityForDomain(domain: String): Int? {
        return when (domain) {
            Light.DOMAIN -> 0
            Cover.DOMAIN -> 1
            Weather.DOMAIN -> 2
            else -> null
        }
    }

    override suspend fun callService(action: Action): List<EntityState> =
        client.callService(action.domain, action.service, action.allParams).getResponseOrThrow()

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
