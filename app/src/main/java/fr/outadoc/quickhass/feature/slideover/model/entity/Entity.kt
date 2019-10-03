package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.FontIcon
import fr.outadoc.quickhass.feature.slideover.model.Action
import fr.outadoc.quickhass.feature.slideover.model.EntityState
import fr.outadoc.quickhass.feature.slideover.model.annotation.StringDomain
import fr.outadoc.quickhass.feature.slideover.model.annotation.StringEntityId

interface Entity {

    val state: EntityState
    val formattedState: String?

    @StringDomain
    val domain: String

    @StringEntityId
    val entityId: String

    val friendlyName: String?
    val icon: FontIcon

    val isVisible: Boolean
    val isEnabled: Boolean
    val isOn: Boolean

    val primaryAction: Action?
}