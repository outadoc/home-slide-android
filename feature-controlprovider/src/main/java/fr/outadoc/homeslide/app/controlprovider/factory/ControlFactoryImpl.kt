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

package fr.outadoc.homeslide.app.controlprovider.factory

import android.content.Context
import android.os.Build
import android.service.controls.Control
import android.service.controls.DeviceTypes
import android.service.controls.templates.ControlButton
import android.service.controls.templates.ControlTemplate
import android.service.controls.templates.ToggleTemplate
import androidx.annotation.RequiresApi
import fr.outadoc.homeslide.app.controlprovider.R
import fr.outadoc.homeslide.app.controlprovider.inject.IntentProvider
import fr.outadoc.homeslide.hassapi.model.entity.Automation
import fr.outadoc.homeslide.hassapi.model.entity.Climate
import fr.outadoc.homeslide.hassapi.model.entity.Cover
import fr.outadoc.homeslide.hassapi.model.entity.Fan
import fr.outadoc.homeslide.hassapi.model.entity.Light
import fr.outadoc.homeslide.hassapi.model.entity.Lock
import fr.outadoc.homeslide.hassapi.model.entity.MediaPlayer
import fr.outadoc.homeslide.hassapi.model.entity.Script
import fr.outadoc.homeslide.hassapi.model.entity.Switch
import fr.outadoc.homeslide.hassapi.model.entity.Vacuum
import fr.outadoc.homeslide.hassapi.model.entity.base.Entity

@RequiresApi(Build.VERSION_CODES.R)
class ControlFactoryImpl(
    private val intentProvider: IntentProvider,
    private val context: Context
) : ControlFactory {

    override fun createStatefulControl(context: Context, entity: Entity): Control? {
        val deviceType = entity.getDeviceType() ?: return null
        val pi = intentProvider.getEntityDetailsActivityIntent(context)
        return Control.StatefulBuilder(entity.entityId, pi)
            .setTitle(entity.friendlyName ?: entity.entityId)
            .setSubtitle("")
            .setDeviceType(deviceType)
            .setControlTemplate(entity.getControlTemplate())
            .setStatus(entity.getStatus())
            .setStatusText(entity.getStatusText())
            .build()
    }

    override fun createControl(context: Context, entity: Entity): Control? {
        val deviceType = entity.getDeviceType() ?: return null
        val pi = intentProvider.getEntityDetailsActivityIntent(context)
        return Control.StatelessBuilder(entity.entityId, pi)
            .setTitle(entity.friendlyName ?: entity.entityId)
            .setSubtitle("")
            .setDeviceType(deviceType)
            .build()
    }

    private fun Entity.getDeviceType(): Int? {
        return when (this) {
            is Automation -> DeviceTypes.TYPE_GENERIC_START_STOP
            is Climate -> DeviceTypes.TYPE_THERMOSTAT
            is Cover -> DeviceTypes.TYPE_BLINDS
            is Fan -> DeviceTypes.TYPE_FAN
            is Light -> DeviceTypes.TYPE_LIGHT
            is Lock -> DeviceTypes.TYPE_GENERIC_LOCK_UNLOCK
            is MediaPlayer -> DeviceTypes.TYPE_TV
            is Script -> DeviceTypes.TYPE_GENERIC_START_STOP
            is Switch -> DeviceTypes.TYPE_SWITCH
            is Vacuum -> DeviceTypes.TYPE_VACUUM
            else -> {
                if (isToggleable) DeviceTypes.TYPE_GENERIC_ON_OFF
                else DeviceTypes.TYPE_UNKNOWN
            }
        }
    }

    private fun Entity.getControlTemplate(): ControlTemplate {
        return if (isToggleable) {
            ToggleTemplate(
                "OnOffTemplate",
                ControlButton(
                    this.isOn,
                    context.getString(R.string.devicecontrol_template_toggle_action_description)
                )
            )
        } else {
            ControlTemplate.getNoTemplateObject()
        }
    }

    private fun Entity.getStatus(): Int {
        return if (!isAvailable) Control.STATUS_NOT_FOUND
        else Control.STATUS_OK
    }

    private fun Entity.getStatusText(): String {
        val formattedState = getFormattedState(context)
        return when {
            !isAvailable -> context.getString(R.string.devicecontrol_template_status_unavailable)
            formattedState != null -> formattedState
            else -> when (this) {
                is Light -> {
                    if (isOn) context.getString(R.string.devicecontrol_template_status_on)
                    else context.getString(R.string.devicecontrol_template_status_off)
                }
                is Cover -> {
                    if (isOn) context.getString(R.string.devicecontrol_template_status_open)
                    else context.getString(R.string.devicecontrol_template_status_closed)
                }
                else -> {
                    if (isOn) context.getString(R.string.devicecontrol_template_status_enabled)
                    else context.getString(R.string.devicecontrol_template_status_disabled)
                }
            }
        }
    }
}
