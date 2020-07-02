package fr.outadoc.homeslide.util.view

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun View?.showSnackbar(message: CharSequence, duration: Int = Snackbar.LENGTH_LONG, block: Snackbar.() -> Unit) {
    if (this == null) return
    Snackbar.make(this, message, duration)
        .apply(block)
        .show()
}

fun View?.showSnackbar(
    message: CharSequence,
    duration: Int = Snackbar.LENGTH_LONG,
    @StringRes actionResId: Int,
    action: () -> Unit
) {
    showSnackbar(message, duration) {
        setAction(actionResId) {
            action()
        }
    }
}
