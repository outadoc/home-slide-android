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
