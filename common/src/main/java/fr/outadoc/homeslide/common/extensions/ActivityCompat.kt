package fr.outadoc.homeslide.common.extensions

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.view.WindowManager

@Suppress("DEPRECATION")
fun Activity.setShowWhenLockedCompat(showWhenLocked: Boolean) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
        val flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON

        if (showWhenLocked) {
            window.addFlags(flags)
        } else {
            window.clearFlags(flags)
        }

    } else {
        setShowWhenLocked(showWhenLocked)
    }
}

fun Activity.isInteractive(): Boolean {
    val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
    return pm.isInteractive
}
