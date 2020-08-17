package fr.outadoc.homeslide.app.controlprovider.factory

import android.os.Build
import android.service.controls.Control
import android.service.controls.DeviceTypes
import android.service.controls.templates.ControlButton
import android.service.controls.templates.ControlTemplate
import android.service.controls.templates.ToggleTemplate
import androidx.annotation.RequiresApi
import fr.outadoc.homeslide.app.controlprovider.inject.DetailsIntentProvider
import fr.outadoc.homeslide.hassapi.model.entity.Cover
import fr.outadoc.homeslide.hassapi.model.entity.Light
import fr.outadoc.homeslide.hassapi.model.entity.base.Entity
import fr.outadoc.homeslide.hassapi.model.entity.base.ToggleableEntity

@RequiresApi(Build.VERSION_CODES.R)
class ControlFactoryImpl(detailsIntentProvider: DetailsIntentProvider) : ControlFactory {

    private val pi = detailsIntentProvider.getEntityDetailsActivityIntent()

    override fun createStatefulControl(entity: Entity): Control? {
        val deviceType = entity.getDeviceType() ?: return null
        return Control.StatefulBuilder(entity.entityId, pi)
            .setTitle(entity.friendlyName ?: entity.entityId)
            .setSubtitle("")
            .setDeviceType(deviceType)
            .setControlTemplate(entity.getControlTemplate())
            .setStatus(entity.getStatus())
            .build()
    }

    override fun createControl(entity: Entity): Control? {
        val deviceType = entity.getDeviceType() ?: return null
        return Control.StatelessBuilder(entity.entityId, pi)
            .setTitle(entity.friendlyName ?: entity.entityId)
            .setSubtitle("")
            .setDeviceType(deviceType)
            .build()
    }

    private fun Entity.getDeviceType(): Int? {
        return when (this) {
            is Light -> DeviceTypes.TYPE_LIGHT
            is Cover -> DeviceTypes.TYPE_BLINDS
            else -> null
        }
    }

    private fun Entity.getControlTemplate(): ControlTemplate {
        return when (this) {
            is ToggleableEntity -> ToggleTemplate("ABinaryEntity", ControlButton(this.isOn, "toggle"))
            is Cover -> ToggleTemplate("CoverEntity", ControlButton(this.isOn, "open/close"))
            else -> ControlTemplate.getNoTemplateObject()
        }
    }

    private fun Entity.getStatus(): Int {
        return Control.STATUS_OK
    }
}
