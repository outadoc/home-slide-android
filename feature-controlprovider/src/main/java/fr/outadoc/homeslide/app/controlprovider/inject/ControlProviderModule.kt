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

package fr.outadoc.homeslide.app.controlprovider.inject

import android.os.Build
import fr.outadoc.homeslide.app.controlprovider.factory.ControlFactory
import fr.outadoc.homeslide.app.controlprovider.factory.ControlFactoryImpl
import fr.outadoc.homeslide.app.controlprovider.repository.ControlRepository
import fr.outadoc.homeslide.app.controlprovider.repository.ControlRepositoryImpl
import fr.outadoc.homeslide.app.controlprovider.vm.ControlsProviderViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun controlProviderModule() = module {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        single<ControlRepository> { ControlRepositoryImpl(get(), get(), get()) }
        single<ControlFactory> { ControlFactoryImpl(get(), get()) }
        viewModel { ControlsProviderViewModel(get()) }
    }
}
