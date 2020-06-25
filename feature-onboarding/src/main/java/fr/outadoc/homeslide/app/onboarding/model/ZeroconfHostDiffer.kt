package fr.outadoc.homeslide.app.onboarding.model

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
