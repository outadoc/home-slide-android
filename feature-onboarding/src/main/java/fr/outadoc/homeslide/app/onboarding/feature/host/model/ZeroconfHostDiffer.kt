package fr.outadoc.homeslide.app.onboarding.feature.host.model

import androidx.recyclerview.widget.DiffUtil
import fr.outadoc.homeslide.app.onboarding.feature.host.model.ZeroconfHost

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
