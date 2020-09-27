package fr.outadoc.homeslide.app.controlprovider.repository

import android.content.Context
import android.os.Build
import android.service.controls.Control
import androidx.annotation.RequiresApi
import fr.outadoc.homeslide.app.controlprovider.factory.ControlFactory
import fr.outadoc.homeslide.hassapi.api.HomeAssistantApi
import fr.outadoc.homeslide.hassapi.factory.EntityFactory
import fr.outadoc.homeslide.hassapi.model.entity.base.Entity
import fr.outadoc.homeslide.hassapi.repository.EntityRepository
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.rest.util.getResponseOrThrow

@RequiresApi(Build.VERSION_CODES.R)
class ControlRepositoryImpl(
    private val client: HomeAssistantApi,
    private val controlFactory: ControlFactory,
    private val entityRepository: EntityRepository
) : ControlRepository {

    override suspend fun getEntities(context: Context): List<Control> {
        return client.getStates()
            .getResponseOrThrow()
            .let { states ->
                states.map { EntityFactory.create(it) }
                    .mapNotNull { entity -> entity.asControl(context) }
            }
    }

    override suspend fun getEntitiesWithState(context: Context): List<Control> {
        return client.getStates()
            .getResponseOrThrow()
            .let { states ->
                states.map { EntityFactory.create(it) }
                    .mapNotNull { entity -> entity.asStatefulControl(context) }
            }
    }

    override suspend fun toggleEntity(entityId: String) {
        try {
            client.getStates()
                .getResponseOrThrow()
                .let { states ->
                    states.map { EntityFactory.create(it) }
                }
                .firstOrNull { entity ->
                    entity.entityId == entityId
                }?.primaryAction?.let { action ->
                    entityRepository.callService(action)
                }
        } catch (e: Exception) {
            KLog.e(e)
        }
    }

    private fun Entity.asControl(context: Context): Control? {
        return controlFactory.createControl(context, this)
    }

    private fun Entity.asStatefulControl(context: Context): Control? {
        return controlFactory.createStatefulControl(context, this)
    }
}
