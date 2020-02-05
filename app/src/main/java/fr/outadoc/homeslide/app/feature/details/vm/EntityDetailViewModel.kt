package fr.outadoc.homeslide.app.feature.details.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.outadoc.homeslide.common.feature.hass.model.entity.Entity

class EntityDetailViewModel : ViewModel() {

    private val _entity = MutableLiveData<Entity>()
    val entity: LiveData<Entity> = _entity

    fun setEntity(entity: Entity) {
        _entity.value = entity
    }
}