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

package fr.outadoc.homeslide.common.feature.grid.ui

import androidx.recyclerview.widget.DiffUtil
import fr.outadoc.homeslide.hassapi.model.Tile

class TileDiffer<T> : DiffUtil.ItemCallback<Tile<T>>() {

    override fun areItemsTheSame(oldItem: Tile<T>, newItem: Tile<T>): Boolean {
        return oldItem.source == newItem.source
    }

    override fun areContentsTheSame(oldItem: Tile<T>, newItem: Tile<T>): Boolean {
        return oldItem == newItem
    }
}
