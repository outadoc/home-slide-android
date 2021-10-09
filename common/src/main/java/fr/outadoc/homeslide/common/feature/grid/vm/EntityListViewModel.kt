/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.outadoc.homeslide.common.feature.grid.vm

import fr.outadoc.homeslide.common.feature.auth.InvalidRefreshTokenException
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import fr.outadoc.homeslide.hassapi.model.PersistedEntity
import fr.outadoc.homeslide.hassapi.model.Tile
import fr.outadoc.homeslide.hassapi.model.entity.base.Entity
import fr.outadoc.homeslide.hassapi.repository.EntityRepository
import fr.outadoc.homeslide.logging.KLog
import io.uniflow.android.AndroidDataFlow
import io.uniflow.core.flow.actionOn
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import io.uniflow.core.threading.onDefault
import io.uniflow.core.threading.onIO
import io.uniflow.core.threading.onMain

class EntityListViewModel(
    private val prefs: GlobalPreferenceRepository,
    private val repository: EntityRepository,
    private val resourceManager: EntityListResourceManager
) : AndroidDataFlow(defaultState = State.InitialLoading) {

    sealed class State : UIState() {
        data class InitialError(val errorMessage: String?) : State()
        object InitialLoading : State()

        data class Editing(
            val allTiles: List<Tile<Entity>>,
            val filter: String
        ) : State() {
            val displayTiles = allTiles.filter { tile ->
                tile.label.contains(filter, ignoreCase = true)
            }
        }

        data class Content(val allTiles: List<Tile<Entity>>) : State() {
            val displayTiles = allTiles.filter { !it.isHidden }
        }
    }

    sealed class Event : UIEvent() {
        data class Error(val e: Throwable, val isInitialLoad: Boolean) : Event()
        data class NotifyUser(val message: String) : Event()
        object StartOnboarding : Event()
        object LoggedOut : Event()
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

        if (currentState is State.InitialError) {
            setState { State.InitialLoading }
        }

        onIO {
            try {
                val tiles = repository.getEntityTiles()
                onDefault {
                    setState {
                        if (tiles.isNullOrEmpty()) {
                            State.InitialError(null)
                        } else {
                            State.Content(tiles)
                        }
                    }
                }
            } catch (e: Exception) {
                KLog.e(e) { "Error while loading entity list" }

                sendEvent {
                    when (e) {
                        is InvalidRefreshTokenException -> Event.LoggedOut
                        else -> Event.Error(e, isInitialLoad = currentState !is State.Content)
                    }
                }

                if (currentState is State.InitialLoading) {
                    setState { State.InitialError(e.localizedMessage) }
                }
            }
        }
    }

    fun onEntityClick(item: Entity) = actionOn<State.Content> { currentState ->
        val action = item.primaryAction ?: return@actionOn
        onDefault {
            setState {
                onEntityLoadStart(currentState, item)
            }
        }

        onIO {
            try {
                repository.callService(action)

                onDefault {
                    setState { onEntityLoadStop(currentState, item, failure = false) }
                }

                onMain { loadEntities() }
            } catch (e: Exception) {
                KLog.e(e) { "Error while executing action" }

                onDefault {
                    setState { onEntityLoadStop(currentState, item, failure = true) }
                }

                sendEvent {
                    when (e) {
                        is InvalidRefreshTokenException -> Event.LoggedOut
                        else -> Event.Error(e, isInitialLoad = false)
                    }
                }
            }
        }
    }

    fun onReorderedEntities(items: List<Tile<Entity>>) =
        actionOn<State.Editing> { currentState ->
            onDefault {
                setState { currentState.copy(allTiles = items) }
            }
        }

    fun enterEditMode() = actionOn<State.Content> { currentState ->
        onDefault {
            setState {
                State.Editing(
                    allTiles = currentState.allTiles.sortByVisibility(),
                    filter = ""
                )
            }
        }
    }

    fun onFilterChange(filter: String) = actionOn<State.Editing> { currentState ->
        onDefault {
            setState { currentState.copy(filter = filter) }
        }
    }

    fun exitEditMode() = actionOn<State.Editing> { currentState ->
        onIO {
            updateEntityDatabase(currentState)

            onDefault {
                setState { State.Content(currentState.allTiles) }
            }
        }
    }

    fun showAll() = actionOn<State.Editing> { currentState ->
        onDefault {
            setState {
                val newList = currentState.allTiles.map { tile ->
                    if (tile in currentState.displayTiles) {
                        tile.copy(isHidden = false)
                    } else tile
                }

                currentState.copy(allTiles = newList)
            }
        }
    }

    fun hideAll() = actionOn<State.Editing> { currentState ->
        onDefault {
            setState {
                val newList = currentState.allTiles.map { tile ->
                    if (tile in currentState.displayTiles) {
                        tile.copy(isHidden = true)
                    } else tile
                }

                currentState.copy(allTiles = newList)
            }
        }
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
                    isActivated = if (failure) tile.isActivated else tile.source.isOn
                )
                else -> tile
            }
        }

        return currentState.copy(allTiles = newList)
    }

    fun onItemVisibilityChange(entity: Entity, isVisible: Boolean) =
        actionOn<State.Editing> { currentState ->
            onDefault {
                setState {
                    val newList = currentState.allTiles.map { tile ->
                        when (tile.source) {
                            entity -> tile.copy(isHidden = !isVisible)
                            else -> tile
                        }
                    }.sortByVisibility()

                    currentState.copy(allTiles = newList)
                }
            }

            entity.friendlyName?.let { entityName ->
                sendEvent {
                    Event.NotifyUser(
                        if (isVisible) {
                            resourceManager.getEntityVisibleMessage(entityName)
                        } else {
                            resourceManager.getEntityHiddenMessage(entityName)
                        }
                    )
                }
            }
        }

    private suspend fun updateEntityDatabase(currentState: State.Editing) {
        currentState.allTiles.mapIndexed { idx, item ->
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

    private fun List<Tile<Entity>>.sortByVisibility() = sortedBy { it.isHidden }
}
