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

package fr.outadoc.homeslide.wear.feature.list

import android.content.Context
import fr.outadoc.homeslide.hassapi.model.Action
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.base.Entity
import fr.outadoc.mdi.FontIcon

class PlaceholderEntity(
    val id: String,
    override val icon: FontIcon,
    override val friendlyName: String
) : Entity {

    companion object {
        private const val DOMAIN = "_placeholder"
    }

    override val state: EntityState get() = throw NotImplementedError()

    override val domain: String = DOMAIN
    override val entityId: String = "$DOMAIN.$id"
    override val isVisible: Boolean = true
    override val isToggleable: Boolean = true
    override val isOn: Boolean = false
    override val primaryAction: Action? = null
    override val isAvailable: Boolean = true
    override fun getFormattedState(context: Context): String? = null
}
