package fr.outadoc.homeslide.app.feature.slideover.ui

import androidx.fragment.app.Fragment

interface SlideOverNavigator {
    fun navigateTo(fragment: Fragment)
    fun updatePeekHeight(peekHeight: Int)
    fun setIsExpandable(isExpandable: Boolean)
}
