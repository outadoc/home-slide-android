package fr.outadoc.homeslide.hassapi.model.entity.base

import android.content.Context
import fr.outadoc.homeslide.hassapi.model.Action
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.annotation.StringDomain
import fr.outadoc.homeslide.hassapi.model.annotation.StringEntityId
import fr.outadoc.homeslide.hassapi.model.annotation.StringState
import fr.outadoc.mdi.FontIcon
import fr.outadoc.mdi.toIconOrNull

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

    override val isToggleable: Boolean
        get() = primaryAction != null

    override val isOn: Boolean = false

    override val primaryAction: Action? = null

    override fun getFormattedState(context: Context): String? {
        return null
    }

    /**
     * Can be overridden by children to provide a contextual icon.
     * e.g. different icon for different weather
     */
    open val fallbackIcon: FontIcon? = null

    override val icon: FontIcon
        get() = state.attributes.icon?.toIconOrNull() ?: fallbackIcon ?: defaultIcon

    protected val additionalAttributes = state.attributes

    fun supportsFeature(feature: Int): Boolean {
        return (additionalAttributes.supportedFeatures and feature) > 0
    }

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
