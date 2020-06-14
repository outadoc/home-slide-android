package fr.outadoc.homeslide.app.controlprovider.inject

import android.os.Build
import fr.outadoc.homeslide.app.controlprovider.factory.ControlFactory
import fr.outadoc.homeslide.app.controlprovider.factory.ControlFactoryImpl
import fr.outadoc.homeslide.app.controlprovider.repository.ControlRepository
import fr.outadoc.homeslide.app.controlprovider.repository.ControlRepositoryImpl
import fr.outadoc.homeslide.app.controlprovider.vm.ControlsProviderViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun controlProviderModule() = module {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        single<ControlRepository> { ControlRepositoryImpl(get(), get(), get()) }
        single<ControlFactory> { ControlFactoryImpl(get()) }
        viewModel { ControlsProviderViewModel(get()) }
    }
}