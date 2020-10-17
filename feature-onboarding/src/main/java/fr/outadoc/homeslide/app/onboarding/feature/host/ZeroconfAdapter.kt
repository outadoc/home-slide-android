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

package fr.outadoc.homeslide.app.onboarding.feature.host

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.homeslide.app.onboarding.R
import fr.outadoc.homeslide.app.onboarding.feature.host.model.ZeroconfHost
import fr.outadoc.homeslide.app.onboarding.feature.host.model.ZeroconfHostDiffer

class ZeroconfAdapter(val onItemClick: (ZeroconfHost) -> Unit, val onItemCountChanged: () -> Unit) :
    ListAdapter<ZeroconfHost, ZeroconfAdapter.ViewHolder>(ZeroconfHostDiffer) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_zeroconf_host, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        with(holder) {
            instanceName.text = item.instanceName
            version.text = item.version
            ip.text = item.hostName
            baseUrl.text = item.baseUrl

            view.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCurrentListChanged(
        previousList: MutableList<ZeroconfHost>,
        currentList: MutableList<ZeroconfHost>
    ) {
        super.onCurrentListChanged(previousList, currentList)

        if (previousList.count() != currentList.count()) {
            onItemCountChanged()
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val instanceName: TextView = view.findViewById(R.id.textView_host_instanceName)
        val version: TextView = view.findViewById(R.id.textView_host_version)
        val ip: TextView = view.findViewById(R.id.textView_host_ip)
        val baseUrl: TextView = view.findViewById(R.id.textView_host_baseUrl)
    }
}
