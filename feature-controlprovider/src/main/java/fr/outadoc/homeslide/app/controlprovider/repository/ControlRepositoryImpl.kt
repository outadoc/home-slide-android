package fr.outadoc.homeslide.app.controlprovider.repository

import android.os.Build
import android.service.controls.Control
import androidx.annotation.RequiresApi
import fr.outadoc.homeslide.app.controlprovider.factory.ControlFactory
import fr.outadoc.homeslide.hassapi.api.HomeAssistantApi
import fr.outadoc.homeslide.hassapi.factory.EntityFactory
import fr.outadoc.homeslide.hassapi.model.entity.base.Entity
import fr.outadoc.homeslide.hassapi.repository.EntityRepository
import fr.outadoc.homeslide.rest.util.wrapResponse

@RequiresApi(Build.VERSION_CODES.R)
class ControlRepositoryImpl(
    private val client: HomeAssistantApi,
    private val controlFactory: ControlFactory,
    private val entityRepository: EntityRepository
) : ControlRepository {

    override suspend fun getEntities(): Result<List<Control>> {
        return wrapResponse { client.getStates() }
            .map { states ->
                states.map { EntityFactory.create(it) }
                    .mapNotNull { entity -> entity.asControl() }
            }
    }

    override suspend fun getEntitiesWithState(): Result<List<Control>> {
        return wrapResponse { client.getStates() }
            .map { states ->
                states.map { EntityFactory.create(it) }
                    .mapNotNull { entity -> entity.asStatefulControl() }
            }
    }

    override suspend fun toggleEntity(entityId: String) {
        wrapResponse { client.getStates() }
            .map { states ->
                states.map { EntityFactory.create(it) }
            }
            .onSuccess { entities ->
                entities.firstOrNull { entity ->
                    entity.entityId == entityId
                }?.primaryAction?.let { action ->
                    entityRepository.callService(action)
                }
            }
    }

    private fun Entity.asControl(): Control? {
        return controlFactory.createControl(this)
    }

    private fun Entity.asStatefulControl(): Control? {
        return controlFactory.createStatefulControl(this)
    }
}
