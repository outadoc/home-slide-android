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

package fr.outadoc.homeslide.hassapi.model.entity

import fr.outadoc.homeslide.hassapi.model.Action
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.base.BaseEntity
import fr.outadoc.mdi.toIcon

class Cover(state: EntityState) : BaseEntity(state, "window-open".toIcon()) {

    companion object {
        const val DOMAIN = "cover"
    }

    override val isOn: Boolean = stateStr == "open"

    override val primaryAction: Action?
        get() = when (stateStr) {
            "open" -> Action(DOMAIN, "close_cover", entityId)
            "closed" -> Action(DOMAIN, "open_cover", entityId)
            else -> null
        }
}
