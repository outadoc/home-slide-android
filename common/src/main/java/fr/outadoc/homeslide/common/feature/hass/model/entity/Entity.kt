package fr.outadoc.homeslide.common.feature.hass.model.entity

import android.content.Context
import fr.outadoc.homeslide.common.feature.hass.model.Action
import fr.outadoc.homeslide.common.feature.hass.model.EntityState
import fr.outadoc.homeslide.common.feature.hass.model.annotation.StringDomain
import fr.outadoc.homeslide.common.feature.hass.model.annotation.StringEntityId
import fr.outadoc.mdi.FontIcon

interface Entity {

    val state: EntityState

    @StringDomain
    val domain: String

    @StringEntityId
    val entityId: String

    val friendlyName: String?
    val icon: FontIcon

    val isVisible: Boolean
    val isToggleable: Boolean
    val isOn: Boolean

    val primaryAction: Action?

    fun getFormattedState(context: Context): String?
}