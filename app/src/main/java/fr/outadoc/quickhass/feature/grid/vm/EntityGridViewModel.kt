package fr.outadoc.quickhass.feature.grid.vm

import androidx.lifecycle.*
import fr.outadoc.quickhass.feature.slideover.model.Tile
import fr.outadoc.quickhass.feature.slideover.rest.EntityRepository
import fr.outadoc.quickhass.model.Action
import fr.outadoc.quickhass.model.entity.Entity
import fr.outadoc.quickhass.persistence.EntityDatabase
import fr.outadoc.quickhass.persistence.model.PersistedEntity
import fr.outadoc.quickhass.preferences.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EntityGridViewModel(
    private val prefs: PreferenceRepository,
    private val repository: EntityRepository,
    private val db: EntityDatabase
) : ViewModel() {

    sealed class EditionState {
        object Disabled : EditionState()
        object Normal : EditionState()
        object Editing : EditionState()
    }

    sealed class GridState {
        object Content : GridState()
        object Skeleton : GridState()
        object NoContent : GridState()
    }

    private val _result = MutableLiveData<Result<Any>>()
    val result: LiveData<Result<Any>> = _result

    private val _allTiles = MutableLiveData<List<Tile<Entity>>>()

    private val _editionState: MutableLiveData<EditionState> = MutableLiveData(EditionState.Disabled)
    val editionState: LiveData<EditionState> = _editionState

    private val _gridState: MutableLiveData<GridState> = MutableLiveData(GridState.Skeleton)
    val gridState: LiveData<GridState> = _gridState

    private val _tiles =
        MediatorLiveData<List<Tile<Entity>>>().apply {
            addSource(_allTiles) { allTiles ->
                value = when (_editionState.value) {
                    EditionState.Editing -> allTiles
                    else -> allTiles.filter { !it.isHidden }
                }
            }

            addSource(_editionState) { state ->
                value = when (state) {
                    EditionState.Editing -> _allTiles.value
                    else -> _allTiles.value?.filter { !it.isHidden }
                }
            }
        }

    val tiles: LiveData<List<Tile<Entity>>> = _tiles

    private val _shouldAskForInitialValues = MutableLiveData<Boolean>()
    val shouldAskForInitialValues: LiveData<Boolean> = _shouldAskForInitialValues

    val refreshIntervalSeconds: Long
        get() = prefs.refreshIntervalSeconds

    fun loadShortcuts() {
        if (!prefs.isOnboardingDone) {
            _shouldAskForInitialValues.value = !prefs.isOnboardingDone
            return
        }

        _gridState.value = if (_tiles.value.isNullOrEmpty()) {
            GridState.Skeleton
        } else {
            GridState.Content
        }

        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.getEntityTiles()
                .onSuccess { tiles ->
                    _editionState.postValue(
                        if (tiles.isEmpty()) {
                            EditionState.Disabled
                        } else {
                            EditionState.Normal
                        }
                    )

                    _allTiles.postValue(tiles)

                    _gridState.postValue(
                        if (tiles.isNullOrEmpty()) {
                            GridState.NoContent
                        } else {
                            GridState.Content
                        }
                    )
                }
                .onFailure {
                    _gridState.postValue(
                        if (_allTiles.value.isNullOrEmpty()) {
                            GridState.NoContent
                        } else {
                            GridState.Content
                        }
                    )
                }

            _result.postValue(res)
        }
    }

    fun onEntityClick(item: Entity) {
        if (item.primaryAction == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            onEntityLoadStart(item)

            repository.callService(item.primaryAction as Action)
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        loadShortcuts()
                    }
                }.onFailure {
                    _result.postValue(Result.failure(it))
                }

            onEntityLoadStop(item)
        }
    }

    fun onReorderedEntities(items: List<Tile<Entity>>) {
        if (items.isEmpty()) {
            return
        }

        _allTiles.postValue(items)
    }

    fun onEditClick() {
        _editionState.value = when (editionState.value!!) {
            EditionState.Normal -> EditionState.Editing

            EditionState.Editing -> {
                viewModelScope.launch(Dispatchers.IO) {
                    updateEntityDatabase()
                }

                EditionState.Normal
            }

            EditionState.Disabled -> EditionState.Disabled
        }
    }

    fun onEntityLoadStart(entity: Entity) {
        updateItems { tile ->
            when (tile.source) {
                entity -> {
                    tile.copy(
                        isLoading = true,
                        isActivated = !tile.isActivated
                    )
                }
                else -> tile
            }
        }
    }

    fun onEntityLoadStop(entity: Entity) {
        updateItems { tile ->
            when (tile.source) {
                entity -> tile.copy(isLoading = false)
                else -> tile
            }
        }
    }

    fun onItemVisibilityChange(entity: Entity, isVisible: Boolean) {
        updateItems { tile ->
            when (tile.source) {
                entity -> tile.copy(isHidden = !isVisible)
                else -> tile
            }
        }
    }

    private fun updateItems(map: (Tile<Entity>) -> Tile<Entity>) {
        _allTiles.postValue(
            _allTiles.value?.map(map)
        )
    }

    private suspend fun updateEntityDatabase() {
        tiles.value?.mapIndexed { idx, item ->
            PersistedEntity(item.source.entityId, idx, item.isHidden)
        }?.let { toBePersisted ->
            // Update database
            db.entityDao().replaceAll(toBePersisted)
        }
    }
}