package fr.outadoc.homeslide.common.inject

import android.app.ActivityManager
import android.content.Context
import android.net.nsd.NsdManager
import androidx.core.content.getSystemService
import kotlinx.datetime.Clock
import org.koin.dsl.module

fun Context.systemModule() = module {
    single { getSystemService<NsdManager>() }
    single { getSystemService<ActivityManager>() }
    single<Clock> { Clock.System }
}
