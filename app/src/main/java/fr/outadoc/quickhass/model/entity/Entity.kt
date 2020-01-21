package fr.outadoc.quickhass.model.entity

import android.content.Context
import fr.outadoc.mdi.FontIcon
import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.model.EntityState
import fr.outadoc.quickhass.model.annotation.StringDomain
import fr.outadoc.quickhass.model.annotation.StringEntityId

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