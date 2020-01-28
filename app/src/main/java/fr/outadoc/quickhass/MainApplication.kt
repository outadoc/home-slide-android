package fr.outadoc.quickhass

import android.app.ActivityManager
import android.app.Application
import android.net.nsd.NsdManager
import android.os.Vibrator
import androidx.core.content.getSystemService
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.github.ajalt.timberkt.Timber.tag
import com.github.ajalt.timberkt.d
import com.squareup.moshi.Moshi
import fr.outadoc.mdi.MaterialIconAssetMapperImpl
import fr.outadoc.mdi.MaterialIconLocator
import fr.outadoc.quickhass.feature.details.vm.EntityDetailViewModel
import fr.outadoc.quickhass.feature.grid.vm.EntityGridViewModel
import fr.outadoc.quickhass.feature.onboarding.rest.DiscoveryApi
import fr.outadoc.quickhass.feature.onboarding.rest.DiscoveryRepository
import fr.outadoc.quickhass.feature.onboarding.rest.DiscoveryRepositoryImpl
import fr.outadoc.quickhass.feature.onboarding.rest.HassZeroconfDiscoveryServiceImpl
import fr.outadoc.quickhass.feature.onboarding.rest.SimpleRestClient
import fr.outadoc.quickhass.feature.onboarding.rest.ZeroconfDiscoveryService
import fr.outadoc.quickhass.feature.onboarding.vm.AuthSetupViewModel
import fr.outadoc.quickhass.feature.onboarding.vm.HostSetupViewModel
import fr.outadoc.quickhass.feature.onboarding.vm.ShortcutSetupViewModel
import fr.outadoc.quickhass.feature.onboarding.vm.SuccessViewModel
import fr.outadoc.quickhass.feature.onboarding.vm.WelcomeViewModel
import fr.outadoc.quickhass.feature.slideover.TileFactory
import fr.outadoc.quickhass.feature.slideover.TileFactoryImpl
import fr.outadoc.quickhass.feature.slideover.json.SkipBadElementsListAdapter
import fr.outadoc.quickhass.feature.slideover.rest.EntityRepository
import fr.outadoc.quickhass.feature.slideover.rest.EntityRepositoryImpl
import fr.outadoc.quickhass.feature.slideover.rest.HomeAssistantApi
import fr.outadoc.quickhass.feature.slideover.rest.RestClient
import fr.outadoc.quickhass.persistence.EntityDatabase
import fr.outadoc.quickhass.preferences.PreferenceRepository
import fr.outadoc.quickhass.preferences.PreferenceRepositoryImpl
import fr.outadoc.quickhass.rest.AccessTokenProvider
import fr.outadoc.quickhass.rest.LongLivedTokenProviderImpl
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

@Suppress("unused")
class MainApplication : Application() {

    private val appModule = module {
        single { getSystemService<NsdManager>() }
        single { getSystemService<Vibrator>() }
        single { getSystemService<ActivityManager>() }

        single { RestClient.create<HomeAssistantApi>(get(), get(), get(), get(), get()) }
        single { SimpleRestClient.create<DiscoveryApi>(get(), get()) }

        single { DiscoveryRepositoryImpl(get()) as DiscoveryRepository }
        single { EntityRepositoryImpl(get(), get(), get()) as EntityRepository }
        single { PreferenceRepositoryImpl(get()) as PreferenceRepository }
        single {
            Room.databaseBuilder(get(), EntityDatabase::class.java, EntityDatabase.DB_NAME)
                .addMigrations(EntityDatabase.MIGRATION_1_2)
                .build()
        }
        single { HassZeroconfDiscoveryServiceImpl(get()) as ZeroconfDiscoveryService }
        single { LongLivedTokenProviderImpl(get()) as AccessTokenProvider }
        single { TileFactoryImpl(get()) as TileFactory }

        single {
            Moshi.Builder()
                .add(SkipBadElementsListAdapter.newFactory())
                .build()
        }

        single {
            HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                tag("OkHttp").d { it }
            }).apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }
        single { ChuckerInterceptor(get()) }

        viewModel { WelcomeViewModel() }
        viewModel { HostSetupViewModel(get(), get(), get()) }
        viewModel { AuthSetupViewModel(get(), get()) }
        viewModel { ShortcutSetupViewModel() }
        viewModel { SuccessViewModel(get(), get()) }

        viewModel { EntityGridViewModel(get(), get(), get()) }
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
            modules(appModule)
        }

        MaterialIconLocator.instance = MaterialIconAssetMapperImpl(applicationContext)
    }
}