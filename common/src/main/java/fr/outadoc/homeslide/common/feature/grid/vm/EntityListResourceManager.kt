package fr.outadoc.homeslide.common.feature.grid.vm

import android.content.Context
import fr.outadoc.homeslide.common.R

class EntityListResourceManager(val context: Context) {

    fun getEntityHiddenMessage(entityName: String): String {
        return context.getString(R.string.grid_entity_hidden_message, entityName)
    }

    fun getEntityVisibleMessage(entityName: String): String {
        return context.getString(R.string.grid_entity_visible_message, entityName)
    }
}