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

package fr.outadoc.homeslide.hassapi.factory

import android.content.Context
import fr.outadoc.homeslide.hassapi.model.Tile
import fr.outadoc.homeslide.hassapi.model.entity.base.Entity

class TileFactoryImpl(private val context: Context) :
    TileFactory {

    override fun create(entity: Entity): Tile<Entity> {
        val state = entity.getFormattedState(context)
        return Tile(
            source = entity,
            isActivated = entity.isOn,
            isToggleable = entity.isToggleable,
            label = entity.friendlyName ?: entity.entityId,
            state = state,
            icon = if (state == null) entity.icon.unicodePoint else null,
            isLoading = false,
            isHidden = false
        )
    }
}
