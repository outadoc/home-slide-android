package fr.outadoc.homeslide.app.overlay.ui

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import fr.outadoc.homeslide.app.overlay.R
import fr.outadoc.homeslide.hassapi.model.EntityState

class OverlayFragment private constructor() : Fragment(R.layout.fragment_overlay_container) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val entityState = arguments?.getParcelable<EntityState>(ARG_STATE)
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