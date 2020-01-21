package fr.outadoc.quickhass.feature.slideover.rest

import fr.outadoc.quickhass.feature.slideover.TileFactory
import fr.outadoc.quickhass.feature.slideover.model.Tile
import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.model.EntityState
import fr.outadoc.quickhass.model.Service
import fr.outadoc.quickhass.model.entity.Entity
import fr.outadoc.quickhass.model.entity.EntityFactory
import fr.outadoc.quickhass.persistence.EntityDatabase
import fr.outadoc.quickhass.preferences.PreferenceRepository
import fr.outadoc.quickhass.rest.AccessTokenProvider
import okhttp3.logging.HttpLoggingInterceptor

class EntityRepositoryImpl(
    private val db: EntityDatabase,
    private val tileFactory: TileFactory,
    loggingInterceptor: HttpLoggingInterceptor,
    accessTokenProvider: AccessTokenProvider,
    prefs: PreferenceRepository
) : EntityRepository {

    private val client: HomeAssistantApi by lazy {
        RestClient.create<HomeAssistantApi>(loggingInterceptor, accessTokenProvider, prefs)
    }

    override suspend fun getEntityTiles(): Result<List<Tile<Entity>>> {
        val persistedEntities = db.entityDao()
            .getPersistedEntities()
            .map { it.entityId to it }
            .toMap()

        return wrapResponse { client.getStates() }.map { states ->
            states.asSequence()
                .map { EntityFactory.create(it) }
                .sortedWith(
                    compareBy(
                        { persistedEntities[it.entityId]?.order ?: Int.MAX_VALUE },
                        { it.domain })
                )
                .map { entity ->
                    val persistedEntity = persistedEntities[entity.entityId]
                    tileFactory
                        .create(entity)
                        .copy(
                            isHidden = persistedEntity?.hidden
                                    ?: !entity.isVisible || INITIAL_DOMAIN_BLACKLIST.contains(entity.domain)
                        )
                }
                .toList()
        }
    }

    override suspend fun getServices(): Result<List<Service>> =
        wrapResponse { client.getServices() }

    override suspend fun callService(action: Action): Result<List<EntityState>> =
        wrapResponse { client.callService(action.domain, action.service, action.allParams) }

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