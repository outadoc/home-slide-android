package fr.outadoc.homeslide.common.feature.grid.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.Timber
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import fr.outadoc.homeslide.hassapi.model.Action
import fr.outadoc.homeslide.hassapi.model.PersistedEntity
import fr.outadoc.homeslide.hassapi.model.Tile
import fr.outadoc.homeslide.hassapi.model.entity.Entity
import fr.outadoc.homeslide.hassapi.repository.EntityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EntityGridViewModel(
    private val prefs: GlobalPreferenceRepository,
    private val repository: EntityRepository
) : ViewModel() {

    sealed class EditionState {
        object Disabled : EditionState()
        object Normal : EditionState()
        object Editing : EditionState()
    }

    sealed class GridState {
        object Content : GridState()
        object Loading : GridState()
        object NoContent : GridState()
    }

    private val _result = MutableLiveData<Result<Any>>()
    val result: LiveData<Result<Any>> = _result

    private val _allTiles = MutableLiveData<List<Tile<Entity>>>()

    private val _editionState: MutableLiveData<EditionState> =
        MutableLiveData(EditionState.Disabled)
    val editionState: LiveData<EditionState> = _editionState

    private val _gridState: MutableLiveData<GridState> = MutableLiveData(GridState.Loading)
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
            GridState.Loading
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
                .onFailure { e ->
                    Timber.e(e) { "Error while loading entity list" }
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
                }.onFailure { e ->
                    Timber.e(e) { "Error while executing action" }
                    _result.postValue(Result.failure(e))
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

    private fun onEntityLoadStart(entity: Entity) {
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

    private fun onEntityLoadStop(entity: Entity) {
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

    fun onBackPressed() {
        if (_editionState.value == EditionState.Editing) {
            onEditClick()
        }
    }

    private fun updateItems(map: (Tile<Entity>) -> Tile<Entity>) {
        _allTiles.postValue(
            _allTiles.value?.map(map)
        )
    }

    private suspend fun updateEntityDatabase() {
        tiles.value?.mapIndexed { idx, item ->
            PersistedEntity(
                item.source.entityId,
                idx,
                item.isHidden
            )
        }?.let { toBePersisted ->
            // Update database
            repository.saveEntityListState(toBePersisted)
        }
    }
}