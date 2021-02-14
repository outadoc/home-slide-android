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

package fr.outadoc.homeslide.wear

import android.app.Application
import fr.outadoc.homeslide.common.feature.consent.ConsentPreferenceRepository
import fr.outadoc.homeslide.common.feature.review.InAppReviewManager
import fr.outadoc.homeslide.common.feature.review.NoopInAppReviewManager
import fr.outadoc.homeslide.common.inject.commonModule
import fr.outadoc.homeslide.common.inject.systemModule
import fr.outadoc.homeslide.common.log.KoinCustomLogger
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.common.rest.SimpleApiClientBuilder
import fr.outadoc.homeslide.hassapi.api.AuthApi
import fr.outadoc.homeslide.hassapi.api.HomeAssistantApi
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.rest.ApiClientBuilder
import fr.outadoc.homeslide.rest.NetworkAccessManager
import fr.outadoc.homeslide.rest.baseurl.AltBaseUrlInterceptor
import fr.outadoc.homeslide.rest.baseurl.BaseUrlProvider
import fr.outadoc.homeslide.wear.preferences.DataSyncViewModel
import fr.outadoc.homeslide.wear.preferences.WearPreferenceRepositoryImpl
import fr.outadoc.homeslide.wear.rest.baseurl.WearBaseUrlProvider
import fr.outadoc.mdi.AndroidMdiMapper
import fr.outadoc.mdi.common.MdiMapperLocator
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Suppress("unused")
class WearApplication : Application() {

    private val wearModule = module {
        single { WearPreferenceRepositoryImpl(get()) }
        single<GlobalPreferenceRepository> { get<WearPreferenceRepositoryImpl>() }
        single<UrlPreferenceRepository> { get<WearPreferenceRepositoryImpl>() }
        single<TokenPreferenceRepository> { get<WearPreferenceRepositoryImpl>() }
        single<ConsentPreferenceRepository> { get<WearPreferenceRepositoryImpl>() }

        single<InAppReviewManager> { get<NoopInAppReviewManager>() }

        single { WearBaseUrlProvider(get(), get()) }
        single<BaseUrlProvider> { get<WearBaseUrlProvider>() }
        single<NetworkAccessManager> { get<WearBaseUrlProvider>() }

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

        viewModel { DataSyncViewModel(get(), get(), get(), get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            KLog.enableDebugLogging()
        }

        startKoin {
            KoinCustomLogger()
            androidContext(this@WearApplication)
            modules(systemModule() + commonModule() + wearModule)
        }

        MdiMapperLocator.instance = AndroidMdiMapper(applicationContext)
    }
}
