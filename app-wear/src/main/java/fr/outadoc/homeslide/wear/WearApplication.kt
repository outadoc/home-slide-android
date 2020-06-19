package fr.outadoc.homeslide.wear

import android.app.Application
import fr.outadoc.homeslide.common.inject.commonModule
import fr.outadoc.homeslide.common.inject.systemModule
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.common.rest.SimpleApiClientBuilder
import fr.outadoc.homeslide.hassapi.api.AuthApi
import fr.outadoc.homeslide.hassapi.api.HomeAssistantApi
import fr.outadoc.homeslide.rest.ApiClientBuilder
import fr.outadoc.homeslide.rest.baseurl.AltBaseUrlInterceptor
import fr.outadoc.mdi.MaterialIconAssetMapperImpl
import fr.outadoc.mdi.MaterialIconLocator
import fr.outadoc.homeslide.wear.inject.KoinTimberLogger
import fr.outadoc.homeslide.wear.preferences.WearPreferenceRepositoryImpl
import fr.outadoc.homeslide.wear.preferences.DataSyncViewModel
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

@Suppress("unused")
class WearApplication : Application() {

    private val wearModule = module {
        single { WearPreferenceRepositoryImpl(get()) }
        single<GlobalPreferenceRepository> { get<WearPreferenceRepositoryImpl>() }
        single<UrlPreferenceRepository> { get<WearPreferenceRepositoryImpl>() }
        single<TokenPreferenceRepository> { get<WearPreferenceRepositoryImpl>() }

        single {
            SimpleApiClientBuilder.newBuilder<AuthApi>(get())
                .addInterceptor(AltBaseUrlInterceptor(get()))
                .build()
        }

        single {
            ApiClientBuilder.newBuilder<HomeAssistantApi>(get(), get(), get())
                .addInterceptor(get<HttpLoggingInterceptor>())
                .build()
        }

        viewModel { DataSyncViewModel(get(), get(), get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            KoinTimberLogger()
            androidContext(this@WearApplication)
            modules(systemModule() + commonModule() + wearModule)
        }

        MaterialIconLocator.instance = MaterialIconAssetMapperImpl(applicationContext)
    }
}