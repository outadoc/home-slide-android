package fr.outadoc.homeslide.common.feature.grid.vm

import com.github.ajalt.timberkt.Timber
import fr.outadoc.homeslide.common.feature.auth.InvalidRefreshTokenException
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import fr.outadoc.homeslide.hassapi.model.Action
import fr.outadoc.homeslide.hassapi.model.PersistedEntity
import fr.outadoc.homeslide.hassapi.model.Tile
import fr.outadoc.homeslide.hassapi.model.entity.Entity
import fr.outadoc.homeslide.hassapi.repository.EntityRepository
import io.uniflow.androidx.flow.AndroidDataFlow
import io.uniflow.core.flow.actionOn
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.threading.onIO
import io.uniflow.core.threading.onMain

class EntityListViewModel(
    private val prefs: GlobalPreferenceRepository,
    private val repository: EntityRepository
) : AndroidDataFlow(defaultState = State.Loading) {

    sealed class State : UIState() {
        object Empty : State()
        object Loading : State()
        data class Editing(val tiles: List<Tile<Entity>>) : State()
        data class Content(val allTiles: List<Tile<Entity>>) : State() {
            val displayTiles = allTiles.filter { !it.isHidden }
        }
    }

    sealed class Event : UIEvent() {
        data class Error(val e: Throwable) : Event()
        object StartOnboarding : Event()
    }

    val refreshIntervalSeconds: Long
        get() = prefs.refreshIntervalSeconds

    fun loadEntities() = action { currentState ->
        if (!prefs.isOnboardingDone) {
            sendEvent { Event.StartOnboarding }
            return@action
        }

        if (currentState is State.Editing) {
            return@action
        }

        if (currentState == State.Empty) {
            setState { State.Loading }
        }

        onIO {
            repository.getEntityTiles()
                .onSuccess { tiles ->
                    setState {
                        if (tiles.isNullOrEmpty()) {
                            State.Empty
                        } else {
                            State.Content(tiles)
                        }
                    }
                }
                .onFailure { e ->
                    Timber.e(e) { "Error while loading entity list" }

                    sendEvent {
                        when (e) {
                            is InvalidRefreshTokenException -> Event.StartOnboarding
                            else -> Event.Error(e)
                        }
                    }

                    setState { currentState }
                }
        }
    }

    fun onEntityClick(item: Entity) = actionOn<State.Content> { currentState ->
        if (item.primaryAction == null) {
            return@actionOn
        }

        setState { onEntityLoadStart(currentState, item) }

        onIO {
            repository.callService(item.primaryAction as Action)
                .onSuccess {
                    setState { onEntityLoadStop(currentState, item, failure = false) }
                    onMain { loadEntities() }
                }.onFailure { e ->
                    Timber.e(e) { "Error while executing action" }
                    setState { onEntityLoadStop(currentState, item, failure = true) }
                    sendEvent {
                        when (e) {
                            is InvalidRefreshTokenException -> Event.StartOnboarding
                            else -> Event.Error(e)
                        }
                    }
                }
        }
    }

    fun onReorderedEntities(items: List<Tile<Entity>>) =
        actionOn<State.Editing> { currentState ->
            setState { currentState.copy(tiles = items) }
        }

    fun enterEditMode() = actionOn<State.Content> { currentState ->
        setState { State.Editing(currentState.allTiles) }
    }

    fun exitEditMode() = actionOn<State.Editing> { currentState ->
        onIO {
            updateEntityDatabase(currentState)
        }

        setState { State.Content(currentState.tiles) }
    }

    private fun onEntityLoadStart(
        currentState: State.Content,
        entity: Entity
    ): State.Content {
        val newList = currentState.allTiles.map { tile ->
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

        return currentState.copy(allTiles = newList)
    }

    private fun onEntityLoadStop(
        currentState: State.Content,
        entity: Entity,
        failure: Boolean
    ): State.Content {
        val newList = currentState.allTiles.map { tile ->
            when (tile.source) {
                entity -> tile.copy(
                    isLoading = false,
                    isActivated = if (failure) tile.isActivated else !tile.isActivated
                )
                else -> tile
            }
        }

        return currentState.copy(allTiles = newList)
    }

    fun onItemVisibilityChange(entity: Entity, isVisible: Boolean) =
        actionOn<State.Editing> { currentState ->
            val newList = currentState.tiles.map { tile ->
                when (tile.source) {
                    entity -> tile.copy(isHidden = !isVisible)
                    else -> tile
                }
            }

            setState { currentState.copy(tiles = newList) }
        }

    private suspend fun updateEntityDatabase(currentState: State.Editing) {
        currentState.tiles.mapIndexed { idx, item ->
            PersistedEntity(
                item.source.entityId,
                idx,
                item.isHidden
            )
        }.let { toBePersisted ->
            // Update database
            repository.saveEntityListState(toBePersisted)
        }
    }
}