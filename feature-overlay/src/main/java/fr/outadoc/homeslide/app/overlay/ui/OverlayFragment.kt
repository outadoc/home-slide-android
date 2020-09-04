package fr.outadoc.homeslide.app.overlay.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import fr.outadoc.homeslide.app.overlay.R
import fr.outadoc.homeslide.hassapi.model.EntityState

class OverlayFragment private constructor() : Fragment(R.layout.fragment_overlay_container) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val entityState = requireArguments().getParcelable<EntityState>(ARG_STATE)!!
        return ComposeView(requireContext()).apply {
            setContent {
                ControlOverlay(entityState = entityState)
            }
        }
    }

    companion object {
        private const val ARG_STATE = "ARG_STATE"

        fun newInstance(entity: EntityState): OverlayFragment {
            return OverlayFragment().apply {
                arguments = bundleOf(ARG_STATE to entity)
            }
        }
    }
}