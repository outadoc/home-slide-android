package fr.outadoc.quickhass

import android.app.Application
import android.os.Vibrator
import androidx.room.Room
import fr.outadoc.quickhass.feature.details.vm.EntityDetailViewModel
import fr.outadoc.quickhass.feature.grid.vm.EntityGridViewModel
import fr.outadoc.quickhass.feature.onboarding.rest.DiscoveryRepository
import fr.outadoc.quickhass.feature.onboarding.rest.DiscoveryRepositoryImpl
import fr.outadoc.quickhass.feature.onboarding.vm.*
import fr.outadoc.quickhass.feature.slideover.rest.EntityRepository
import fr.outadoc.quickhass.feature.slideover.rest.EntityRepositoryImpl
import fr.outadoc.quickhass.persistence.EntityDatabase
import fr.outadoc.quickhass.preferences.PreferenceRepository
import fr.outadoc.quickhass.preferences.PreferenceRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module


@Suppress("unused")
class MainApplication : Application() {

    private val appModule = module {
        single { DiscoveryRepositoryImpl() as DiscoveryRepository }
        single { EntityRepositoryImpl(get(), get()) as EntityRepository }
        single { PreferenceRepositoryImpl(get()) as PreferenceRepository }
        single { Room.databaseBuilder(get(), EntityDatabase::class.java, EntityDatabase.DB_NAME).build() }

        single { getSystemService(VIBRATOR_SERVICE) as Vibrator }

        viewModel { WelcomeViewModel() }
        viewModel { HostSetupViewModel(get(), get()) }
        viewModel { AuthSetupViewModel(get(), get()) }
        viewModel { ShortcutSetupViewModel() }
        viewModel { SuccessViewModel(get()) }

        viewModel { EntityGridViewModel(get(), get(), get()) }
        viewModel { EntityDetailViewModel() }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}