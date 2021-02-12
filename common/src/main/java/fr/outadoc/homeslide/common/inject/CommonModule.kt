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

import android.content.Context
import androidx.room.Room
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.wearable.Wearable
import com.squareup.moshi.Moshi
import fr.outadoc.homeslide.common.feature.auth.repository.AppOAuthConfiguration
import fr.outadoc.homeslide.common.feature.auth.repository.AuthRepositoryImpl
import fr.outadoc.homeslide.common.feature.grid.vm.EntityListResourceManager
import fr.outadoc.homeslide.common.feature.grid.vm.EntityListViewModel
import fr.outadoc.homeslide.common.feature.slideover.EntityRepositoryImpl
import fr.outadoc.homeslide.common.json.SkipBadElementsListAdapter
import fr.outadoc.homeslide.common.log.OkHttpCustomLogger
import fr.outadoc.homeslide.common.log.UniFlowCustomLogger
import fr.outadoc.homeslide.common.persistence.EntityDatabase
import fr.outadoc.homeslide.common.preferences.BaseUrlConfigProviderImpl
import fr.outadoc.homeslide.common.rest.TokenProviderImpl
import fr.outadoc.homeslide.common.sync.GoogleDataSyncClient
import fr.outadoc.homeslide.common.sync.NoopDataSyncClient
import fr.outadoc.homeslide.hassapi.factory.TileFactory
import fr.outadoc.homeslide.hassapi.factory.TileFactoryImpl
import fr.outadoc.homeslide.hassapi.repository.AuthRepository
import fr.outadoc.homeslide.hassapi.repository.EntityRepository
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.rest.auth.AccessTokenProvider
import fr.outadoc.homeslide.rest.auth.OAuthConfiguration
import fr.outadoc.homeslide.rest.baseurl.BaseUrlConfigProvider
import io.uniflow.core.logger.UniFlowLogger
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.converter.moshi.MoshiConverterFactory

fun commonModule() = module {
    single<BaseUrlConfigProvider> { BaseUrlConfigProviderImpl(get()) }
    single<AccessTokenProvider> { TokenProviderImpl(get(), get(), get()) }
    single<OAuthConfiguration> { AppOAuthConfiguration() }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(), get(), get()) }

    single<EntityRepository> { EntityRepositoryImpl(get(), get(), get(), get()) }

    single { EntityListResourceManager(get()) }

    single<TileFactory> { TileFactoryImpl(get()) }

    single {
        val apiAvailability = GoogleApiAvailability.getInstance()
        if (apiAvailability.isGooglePlayServicesAvailable(get<Context>()) == ConnectionResult.SUCCESS) {
            GoogleDataSyncClient(get(), Wearable.getDataClient(get<Context>()))
        } else {
            KLog.i { "Google Play Services unavailable, disabling wear sync" }
            NoopDataSyncClient()
        }
    }

    single {
        Room.databaseBuilder(get(), EntityDatabase::class.java, EntityDatabase.DB_NAME)
            .addMigrations(EntityDatabase.MIGRATION_1_2)
            .build()
            .entityDao()
    }

    single {
        HttpLoggingInterceptor(OkHttpCustomLogger()).apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }
    }

    single<Moshi> {
        Moshi.Builder()
            .add(SkipBadElementsListAdapter.newFactory())
            .build()
    }

    single<Converter.Factory> { MoshiConverterFactory.create(get()) }

    viewModel { EntityListViewModel(get(), get(), get()) }

    UniFlowLogger.init(UniFlowCustomLogger())
}
