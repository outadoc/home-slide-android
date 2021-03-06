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

package fr.outadoc.homeslide.common.inject

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.nsd.NsdManager
import androidx.core.content.getSystemService
import kotlinx.datetime.Clock
import org.koin.dsl.module

fun Context.systemModule() = module {
    single { getSystemService<NsdManager>() }
    single { getSystemService<ActivityManager>() }
    single { getSystemService<ConnectivityManager>() }
    single<Clock> { Clock.System }
}
