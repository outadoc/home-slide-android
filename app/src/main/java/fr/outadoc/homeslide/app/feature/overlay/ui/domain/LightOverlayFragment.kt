package fr.outadoc.homeslide.app.feature.overlay.ui.domain

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.hassapi.model.EntityState

class LightOverlayFragment private constructor() : Fragment(R.layout.fragment_overlay_light) {

    companion object {
        private const val ARG_STATE = "ARG_STATE"

        fun newInstance(state: EntityState): LightOverlayFragment {
            return LightOverlayFragment().apply {
                arguments = bundleOf(ARG_STATE to state)
            }
        }
    }
}
