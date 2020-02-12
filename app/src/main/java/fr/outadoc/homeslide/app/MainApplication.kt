package fr.outadoc.homeslide.app

import android.app.ActivityManager
import android.app.Application
import android.net.nsd.NsdManager
import androidx.core.content.getSystemService
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.github.ajalt.timberkt.Timber.tag
import com.github.ajalt.timberkt.d
import com.squareup.moshi.Moshi
import fr.outadoc.homeslide.app.feature.slideover.EntityRepositoryImpl
import fr.outadoc.homeslide.app.inject.KoinTimberLogger
import fr.outadoc.homeslide.app.onboarding.rest.DiscoveryRepositoryImpl
import fr.outadoc.homeslide.app.onboarding.rest.HassZeroconfDiscoveryServiceImpl
import fr.outadoc.homeslide.app.onboarding.rest.SimpleApiClientBuilder
import fr.outadoc.homeslide.app.onboarding.vm.HostSetupViewModel
import fr.outadoc.homeslide.app.onboarding.vm.ShortcutSetupViewModel
import fr.outadoc.homeslide.app.onboarding.vm.SuccessViewModel
import fr.outadoc.homeslide.app.onboarding.vm.WelcomeViewModel
import fr.outadoc.homeslide.app.persistence.EntityDatabase
import fr.outadoc.homeslide.app.preferences.PreferenceRepositoryImpl
import fr.outadoc.homeslide.common.feature.auth.repository.AppOAuthConfiguration
import fr.outadoc.homeslide.common.feature.auth.repository.AuthRepositoryImpl
import fr.outadoc.homeslide.common.feature.details.vm.EntityDetailViewModel
import fr.outadoc.homeslide.common.feature.grid.vm.EntityGridViewModel
import fr.outadoc.homeslide.common.json.SkipBadElementsListAdapter
import fr.outadoc.homeslide.common.preferences.BaseUrlConfigProviderImpl
import fr.outadoc.homeslide.common.preferences.PreferenceRepository
import fr.outadoc.homeslide.common.rest.TokenProviderImpl
import fr.outadoc.homeslide.hassapi.api.AuthApi
import fr.outadoc.homeslide.hassapi.api.DiscoveryApi
import fr.outadoc.homeslide.hassapi.api.HomeAssistantApi
import fr.outadoc.homeslide.hassapi.factory.TileFactory
import fr.outadoc.homeslide.hassapi.factory.TileFactoryImpl
import fr.outadoc.homeslide.hassapi.repository.AuthRepository
import fr.outadoc.homeslide.hassapi.repository.DiscoveryRepository
import fr.outadoc.homeslide.hassapi.repository.EntityRepository
import fr.outadoc.homeslide.rest.ApiClientBuilder
import fr.outadoc.homeslide.rest.auth.AccessTokenProvider
import fr.outadoc.homeslide.rest.auth.OAuthConfiguration
import fr.outadoc.homeslide.rest.baseurl.AltBaseUrlInterceptor
import fr.outadoc.homeslide.rest.baseurl.BaseUrlConfigProvider
import fr.outadoc.homeslide.zeroconf.ZeroconfDiscoveryService
import fr.outadoc.mdi.MaterialIconAssetMapperImpl
import fr.outadoc.mdi.MaterialIconLocator
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

@Suppress("unused")
class MainApplication : Application() {

    private val systemModule = module {
        single { getSystemService<NsdManager>() }
        single { getSystemService<ActivityManager>() }
    }

    private val commonModule = module {
        single { BaseUrlConfigProviderImpl(get()) as BaseUrlConfigProvider }
        single { TokenProviderImpl(get(), get()) as AccessTokenProvider }
        single { AppOAuthConfiguration() as OAuthConfiguration }
        single { AuthRepositoryImpl(get(), get(), get()) as AuthRepository }

        single { TileFactoryImpl(get()) as TileFactory }

        single {
            HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                tag("OkHttp").d { it }
            }).apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

        single { ChuckerInterceptor(get()) }

        single {
            ApiClientBuilder.newBuilder<HomeAssistantApi>(get(), get(), get())
                .addInterceptor(get<HttpLoggingInterceptor>())
                .addInterceptor(get<ChuckerInterceptor>())
                .build()
        }

        single {
            Moshi.Builder()
                .add(SkipBadElementsListAdapter.newFactory())
                .build()
        }

        single { MoshiConverterFactory.create(get()) as Converter.Factory }

        viewModel { EntityGridViewModel(get(), get()) }
    }

    private val appModule = module {
        single {
            SimpleApiClientBuilder.newBuilder<DiscoveryApi>(get())
                .addInterceptor(get<HttpLoggingInterceptor>())
                .addInterceptor(get<ChuckerInterceptor>())
                .build()
        }

        single {
            SimpleApiClientBuilder.newBuilder<AuthApi>(get())
                .addInterceptor(AltBaseUrlInterceptor(get()))
                .addInterceptor(get<HttpLoggingInterceptor>())
                .addInterceptor(get<ChuckerInterceptor>())
                .build()
        }

        single {
            Room.databaseBuilder(get(), EntityDatabase::class.java, EntityDatabase.DB_NAME)
                .addMigrations(EntityDatabase.MIGRATION_1_2)
                .build()
                .entityDao()
        }

        single { HassZeroconfDiscoveryServiceImpl(get()) as ZeroconfDiscoveryService }

        single { DiscoveryRepositoryImpl(get()) as DiscoveryRepository }
        single { PreferenceRepositoryImpl(get()) as PreferenceRepository }
        single { EntityRepositoryImpl(get(), get(), get()) as EntityRepository }


        viewModel { WelcomeViewModel() }
        viewModel { HostSetupViewModel(get(), get(), get(), get(), get()) }
        viewModel { ShortcutSetupViewModel() }
        viewModel { SuccessViewModel(get(), get()) }

        viewModel { EntityDetailViewModel() }
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            KoinTimberLogger()
            androidContext(this@MainApplication)
            modules(listOf(systemModule, commonModule, appModule))
        }

        MaterialIconLocator.instance = MaterialIconAssetMapperImpl(applicationContext)
    }
}