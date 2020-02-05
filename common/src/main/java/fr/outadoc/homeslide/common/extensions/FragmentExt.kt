package fr.outadoc.homeslide.common.extensions

import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

private val Fragment.supportActionBar: ActionBar?
    get() = (activity as? AppCompatActivity)?.supportActionBar

fun Fragment.setupToolbar(@StringRes title: Int, canGoBack: Boolean) {
    supportActionBar?.setTitle(title)
    supportActionBar?.setDisplayHomeAsUpEnabled(canGoBack)
    setHasOptionsMenu(true)
}