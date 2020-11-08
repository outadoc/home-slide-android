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

package fr.outadoc.homeslide.hassapi.model.entity.base

import android.content.Context
import fr.outadoc.homeslide.hassapi.model.Action
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.annotation.StringDomain
import fr.outadoc.homeslide.hassapi.model.annotation.StringEntityId
import fr.outadoc.homeslide.hassapi.model.annotation.StringState
import fr.outadoc.mdi.common.MdiFontIcon
import fr.outadoc.mdi.toIconOrNull

abstract class BaseEntity(
    final override val state: EntityState,
    private val defaultIcon: MdiFontIcon
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
    open val fallbackIcon: MdiFontIcon? = null

    override val icon: MdiFontIcon
        get() = state.attributes.icon?.toIconOrNull() ?: fallbackIcon ?: defaultIcon

    protected val additionalAttributes = state.attributes

    override val isAvailable: Boolean
        get() = state.state != "unavailable"

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
