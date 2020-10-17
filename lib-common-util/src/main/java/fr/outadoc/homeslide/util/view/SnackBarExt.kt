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

package fr.outadoc.homeslide.util.view

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun View?.showSnackbar(
    @StringRes messageResId: Int,
    duration: Int = Snackbar.LENGTH_LONG,
    block: Snackbar.() -> Unit = {}
) {
    this?.apply {
        showSnackbar(context.getString(messageResId), duration, block)
    }
}

fun View?.showSnackbar(
    message: CharSequence,
    duration: Int = Snackbar.LENGTH_LONG,
    block: Snackbar.() -> Unit = {}
) {
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
