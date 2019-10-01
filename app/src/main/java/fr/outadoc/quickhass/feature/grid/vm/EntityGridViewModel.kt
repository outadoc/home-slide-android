package fr.outadoc.quickhass.feature.grid.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.outadoc.quickhass.feature.slideover.model.Action
import fr.outadoc.quickhass.feature.slideover.model.annotation.StringEntityId
import fr.outadoc.quickhass.feature.slideover.model.entity.Entity
import fr.outadoc.quickhass.feature.slideover.rest.EntityRepository
import fr.outadoc.quickhass.persistence.EntityDatabase
import fr.outadoc.quickhass.persistence.model.PersistedEntity
import fr.outadoc.quickhass.preferences.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EntityGridViewModel(
    private val prefs: PreferenceRepository,
    private val repository: EntityRepository,
    private val db: EntityDatabase
) : ViewModel() {

    private val _result = MutableLiveData<Result<List<Entity>>>()
    val result: LiveData<Result<List<Entity>>> = _result

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _shouldAskForInitialValues = MutableLiveData<Boolean>()
    val shouldAskForInitialValues: LiveData<Boolean> = _shouldAskForInitialValues

    private val _isEditingMode = MutableLiveData(false)
    val isEditingMode: LiveData<Boolean> = _isEditingMode

    private val _loadingEntityIds = MutableLiveData<Set<@StringEntityId String>>()
    val loadingEntityIds: LiveData<Set<@StringEntityId String>> = _loadingEntityIds

    fun loadShortcuts() {
        if (!prefs.isOnboardingDone) {
            _shouldAskForInitialValues.value = !prefs.isOnboardingDone
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)

            val res = repository.getEntities()
            _result.postValue(res)

            _isLoading.postValue(false)
        }
    }

    fun onEntityClick(item: Entity) {
        if (item.primaryAction == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            onEntityLoadStart(item)

            repository.callService(item.primaryAction as Action)
                .onSuccess {
                    loadShortcuts()
                }.onFailure {
                    _result.postValue(Result.failure(it))
                }

            onEntityLoadStop(item)
            _isLoading.postValue(false)
        }
    }

    fun onReorderedEntities(items: List<Entity>) {
        if (items.isEmpty()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val toBePersisted = items.mapIndexed { idx, item ->
                PersistedEntity(item.entityId, idx)
            }

            // Update database
            with(db.entityDao()) {
                replaceAll(toBePersisted)
            }
        }
    }

    fun onEntityLoadStart(entity: Entity) {
        val loadingIds = loadingEntityIds.value ?: emptySet()
        _loadingEntityIds.postValue(
            loadingIds.plus(entity.entityId)
        )
    }

    fun onEntityLoadStop(entity: Entity) {
        val loadingIds = loadingEntityIds.value ?: emptySet()
        _loadingEntityIds.postValue(
            loadingIds.minus(entity.entityId)
        )
    }

    fun onEditClick() {
        _isEditingMode.value = _isEditingMode.value != true
    }
}