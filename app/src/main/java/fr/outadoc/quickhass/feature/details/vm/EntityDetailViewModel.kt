package fr.outadoc.quickhass.feature.details.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.outadoc.quickhass.feature.slideover.model.entity.Entity

class EntityDetailViewModel : ViewModel() {

    private val _entity = MutableLiveData<Entity>()
    val entity: LiveData<Entity> = _entity

    fun setEntity(entity: Entity) {
        _entity.value = entity
    }
}