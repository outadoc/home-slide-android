package fr.outadoc.quickhass.feature.onboarding.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.outadoc.quickhass.R
import fr.outadoc.quickhass.feature.onboarding.model.ZeroconfHost
import fr.outadoc.quickhass.feature.onboarding.model.ZeroconfHostDiffer

class ZeroconfAdapter(val onItemClick: (ZeroconfHost) -> Unit) : ListAdapter<ZeroconfHost, ZeroconfAdapter.ViewHolder>(ZeroconfHostDiffer) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_zeroconf_host, parent, false)
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

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val instanceName: TextView = view.findViewById(R.id.textView_host_instanceName)
        val version: TextView = view.findViewById(R.id.textView_host_version)
        val ip: TextView = view.findViewById(R.id.textView_host_ip)
        val baseUrl: TextView = view.findViewById(R.id.textView_host_baseUrl)
    }
}