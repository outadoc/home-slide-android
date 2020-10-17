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

package fr.outadoc.homeslide.wear.feature.list

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.wear.activity.ConfirmationActivity
import androidx.wear.widget.WearableLinearLayoutManager
import fr.outadoc.homeslide.common.feature.grid.vm.EntityListViewModel
import fr.outadoc.homeslide.common.feature.grid.vm.EntityListViewModel.Event
import fr.outadoc.homeslide.common.feature.grid.vm.EntityListViewModel.State
import fr.outadoc.homeslide.hassapi.factory.TileFactory
import fr.outadoc.homeslide.hassapi.model.Tile
import fr.outadoc.homeslide.hassapi.model.entity.base.Entity
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.wear.R
import fr.outadoc.homeslide.wear.databinding.FragmentEntityListBinding
import fr.outadoc.homeslide.wear.feature.about.AboutActivity
import fr.outadoc.mdi.toIcon
import io.uniflow.androidx.flow.onEvents
import io.uniflow.androidx.flow.onStates
import java.util.concurrent.TimeUnit
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class EntityListFragment : Fragment() {

    private val vm: EntityListViewModel by viewModel()
    private val tileFactory: TileFactory by inject()

    private var binding: FragmentEntityListBinding? = null
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val tileAdapter = EntityTileAdapter { entity -> onEntityClick(entity) }

    private lateinit var additionalTiles: List<Tile<Entity>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        additionalTiles = listOf(
            tileFactory.create(
                PlaceholderEntity(
                    id = PLACEHOLDER_ID_ABOUT,
                    icon = "mdi:information".toIcon(),
                    friendlyName = getString(R.string.list_item_about)
                )
            )
        )

        binding = FragmentEntityListBinding.inflate(inflater, container, false).apply {
            wearableRecyclerViewShortcuts.apply {
                layoutManager = WearableLinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = tileAdapter
            }

            layoutNoContent.buttonNoContentRetry.setOnClickListener {
                cancelRefresh()
                vm.loadEntities()
            }
        }

        onStates(vm) { state ->
            when (state) {
                is State.Content -> {
                    binding?.apply {
                        wearableRecyclerViewShortcuts.requestFocus()
                        tileAdapter.submitList(state.displayTiles + additionalTiles)
                    }
                }
                is State.InitialError -> {
                    binding?.layoutNoContent?.textViewNoContentErrorMessage?.apply {
                        text = state.errorMessage
                        isGone = state.errorMessage.isNullOrBlank()
                    }
                }
            }

            binding?.apply {
                val childToDisplay = when (state) {
                    is State.Content -> CHILD_CONTENT
                    State.InitialLoading -> CHILD_LOADING
                    else -> CHILD_NO_CONTENT
                }

                if (viewFlipperEntityList.displayedChild != childToDisplay) {
                    viewFlipperEntityList.displayedChild = childToDisplay
                }

                if (state is State.Content) {
                    wearableRecyclerViewShortcuts.requestFocus()
                }
            }
        }

        onEvents(vm) { event ->
            when (val data = event.take()) {
                is Event.StartOnboarding -> {
                    binding?.apply {
                        layoutNoContent.textViewNoContentErrorMessage.apply {
                            text = getString(R.string.onboarding_required_error)
                            isGone = false
                        }

                        viewFlipperEntityList.displayedChild = CHILD_NO_CONTENT
                    }
                }
                is Event.Error -> {
                    Intent(requireContext(), ConfirmationActivity::class.java).apply {
                        putExtra(
                            ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                            ConfirmationActivity.FAILURE_ANIMATION
                        )
                        putExtra(
                            ConfirmationActivity.EXTRA_MESSAGE,
                            getString(R.string.list_loading_error)
                        )
                        putExtra(
                            ConfirmationActivity.EXTRA_ANIMATION_DURATION_MILLIS,
                            ERROR_DISPLAY_DURATION_MS
                        )
                    }.also { intent ->
                        if (!data.isInitialLoad) {
                            startActivity(intent)
                        }
                    }
                }
            }
        }

        scheduleRefresh()
        return binding?.root
    }

    private fun onEntityClick(entity: Entity) {
        when (entity) {
            is PlaceholderEntity -> {
                startActivity(
                    Intent(
                        requireContext(), when (entity.id) {
                            PLACEHOLDER_ID_ABOUT -> AboutActivity::class.java
                            else -> throw IllegalArgumentException("placeholder id $id is unknown")
                        }
                    )
                )
            }
            else -> vm.onEntityClick(entity)
        }
    }

    override fun onResume() {
        super.onResume()
        vm.loadEntities()
    }

    private fun scheduleRefresh() {
        // Only one refresh scheduled at once
        cancelRefresh()

        KLog.d { "scheduling refresh" }
        handler.postDelayed(TimeUnit.SECONDS.toMillis(vm.refreshIntervalSeconds)) {
            vm.loadEntities()
        }
    }

    private fun cancelRefresh() {
        KLog.d { "canceling refresh" }
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        fun newInstance() = EntityListFragment()

        private const val CHILD_CONTENT = 0
        private const val CHILD_NO_CONTENT = 1
        private const val CHILD_LOADING = 2

        private const val PLACEHOLDER_ID_ABOUT = "about"

        private const val ERROR_DISPLAY_DURATION_MS = 5000
    }
}
