package fr.outadoc.quickhass.feature.slideover.rest

import android.content.Context
import androidx.room.Room
import fr.outadoc.quickhass.feature.slideover.model.*
import fr.outadoc.quickhass.persistence.EntityDatabase
import fr.outadoc.quickhass.persistence.model.PersistedEntity
import fr.outadoc.quickhass.preferences.PreferenceRepository

class EntityRepositoryImpl(context: Context, prefs: PreferenceRepository) : EntityRepository {

    private val db = Room.databaseBuilder(
        context,
        EntityDatabase::class.java, EntityDatabase.DB_NAME
    ).build()

    private val persistedEntityCache: List<PersistedEntity>
        get() = db.entityDao().getPersistedEntities()

    private val entityOrder: Map<String, Int>
        get() = persistedEntityCache.map { it.entityId to it.order }.toMap()

    private val client: HomeAssistantApi by lazy {
        RestClient.create<HomeAssistantApi>(prefs)
    }

    override suspend fun getEntities(): Result<List<Entity>> =
        wrapResponse { client.getStates() }.map { states ->
            states.asSequence()
                .map { EntityFactory.create(it) }
                .filter { it.isVisible }
                .filter { !INITIAL_DOMAIN_BLACKLIST.contains(it.domain) }
                .sortedWith(
                    compareBy(
                        { entityOrder[it.entityId] },
                        { it.domain })
                )
                .toList()
        }

    override suspend fun getServices(): Result<List<Service>> =
        wrapResponse { client.getServices() }

    override suspend fun callService(action: Action): Result<List<State>> =
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