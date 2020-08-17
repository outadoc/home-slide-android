package fr.outadoc.homeslide.app.feature.overlay.ui

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import fr.outadoc.homeslide.app.R
import fr.outadoc.homeslide.app.feature.overlay.ui.domain.OverlayFragmentFactory
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.base.Entity

class OverlayFragment private constructor() : Fragment(R.layout.fragment_overlay_container) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getParcelable<EntityState>(ARG_STATE)?.let { state ->
            childFragmentManager
                .beginTransaction()
                .add(R.id.frameLayout_overlayContent, OverlayFragmentFactory.create(state))
                .commit()
        }
    }

    companion object {
        private const val ARG_STATE = "ARG_ENTITY"

        fun newInstance(entity: Entity): OverlayFragment {
            return OverlayFragment().apply {
                arguments = bundleOf(ARG_STATE to entity)
            }
        }
    }
}