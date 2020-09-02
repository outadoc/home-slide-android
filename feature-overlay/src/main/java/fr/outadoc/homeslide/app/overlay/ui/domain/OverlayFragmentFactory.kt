package fr.outadoc.homeslide.app.overlay.ui.domain

import androidx.fragment.app.Fragment
import fr.outadoc.homeslide.hassapi.model.EntityState
import fr.outadoc.homeslide.hassapi.model.entity.Light

object OverlayFragmentFactory {

    fun create(state: EntityState): Fragment = when (state.domain) {
        Light.DOMAIN -> LightOverlayFragment.newInstance(state)
        else -> throw IllegalArgumentException("domain ${state.domain} has no corresponding overlay")
    }
}