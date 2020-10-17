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

package fr.outadoc.homeslide.app.onboarding.feature.host.model

import androidx.recyclerview.widget.DiffUtil

object ZeroconfHostDiffer : DiffUtil.ItemCallback<ZeroconfHost>() {

    override fun areItemsTheSame(oldItem: ZeroconfHost, newItem: ZeroconfHost): Boolean {
        return oldItem.hostName == newItem.hostName
    }

    override fun areContentsTheSame(oldItem: ZeroconfHost, newItem: ZeroconfHost): Boolean {
        return oldItem.baseUrl == newItem.baseUrl &&
                oldItem.hostName == newItem.hostName &&
                oldItem.instanceName == newItem.instanceName &&
                oldItem.version == newItem.version
    }
}
