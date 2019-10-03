package fr.outadoc.quickhass.feature.slideover.model.entity

import fr.outadoc.mdi.FontIcon
import fr.outadoc.mdi.toIcon
import fr.outadoc.quickhass.feature.slideover.model.Action
import fr.outadoc.quickhass.feature.slideover.model.EntityState
import fr.outadoc.quickhass.feature.slideover.model.annotation.StringDomain
import fr.outadoc.quickhass.feature.slideover.model.annotation.StringEntityId
import fr.outadoc.quickhass.feature.slideover.model.annotation.StringState

abstract class BaseEntity(
    final override val state: EntityState,
    private val defaultIcon: FontIcon
) : Entity {

    @StringEntityId
    override val entityId: String = state.entityId

    @StringState
    protected val stateStr: String = state.state

    @StringDomain
    override val domain: String = state.domain

    override val friendlyName: String? = state.attributes.friendlyName

    override val isVisible: Boolean = !state.attributes.isHidden

    override val isEnabled: Boolean
        get() = primaryAction != null

    override val isOn: Boolean = false

    override val primaryAction: Action? = null

    override val formattedState: String? = null

    /**
     * Can be overridden by children to provide a contextual icon.
     * e.g. different icon for different weather
     */
    open val fallbackIcon: FontIcon? = null

    override val icon: FontIcon
        get() = state.attributes.icon?.toIcon() ?: fallbackIcon ?: defaultIcon

    protected val additionalAttributes = state.attributes

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseEntity

        if (entityId != other.entityId) return false

        return true
    }

    override fun hashCode(): Int {
        return entityId.hashCode()
    }

    override fun toString(): String {
        return entityId
    }
}

