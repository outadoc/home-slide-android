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

package fr.outadoc.homeslide.app

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.android.play.core.review.ReviewManagerFactory
import fr.outadoc.homeslide.app.controlprovider.inject.IntentProvider
import fr.outadoc.homeslide.app.controlprovider.inject.controlProviderModule
import fr.outadoc.homeslide.app.feature.review.GoogleInAppReviewManager
import fr.outadoc.homeslide.app.feature.review.InAppReviewLaunchCounter
import fr.outadoc.homeslide.app.inject.AppIntentProvider
import fr.outadoc.homeslide.app.onboarding.feature.authcallback.AuthCallbackViewModel
import fr.outadoc.homeslide.app.onboarding.feature.host.HostSetupViewModel
import fr.outadoc.homeslide.app.onboarding.feature.host.model.ZeroconfHost
import fr.outadoc.homeslide.app.onboarding.feature.host.rest.DiscoveryRepositoryImpl
import fr.outadoc.homeslide.app.onboarding.feature.host.rest.HassZeroconfDiscoveryServiceImpl
import fr.outadoc.homeslide.app.onboarding.feature.shortcuts.ShortcutSetupViewModel
import fr.outadoc.homeslide.app.onboarding.feature.success.SuccessViewModel
import fr.outadoc.homeslide.app.onboarding.feature.welcome.WelcomeViewModel
import fr.outadoc.homeslide.app.preferences.PreferencePublisher
import fr.outadoc.homeslide.app.preferences.PreferenceRepositoryImpl
import fr.outadoc.homeslide.common.feature.consent.ConsentPreferenceRepository
import fr.outadoc.homeslide.common.feature.daynight.ThemePreferenceRepository
import fr.outadoc.homeslide.common.feature.details.vm.EntityDetailViewModel
import fr.outadoc.homeslide.common.feature.review.InAppReviewManager
import fr.outadoc.homeslide.common.inject.commonModule
import fr.outadoc.homeslide.common.inject.systemModule
import fr.outadoc.homeslide.common.log.KoinCustomLogger
import fr.outadoc.homeslide.common.preferences.GlobalPreferenceRepository
import fr.outadoc.homeslide.common.preferences.TokenPreferenceRepository
import fr.outadoc.homeslide.common.preferences.UrlPreferenceRepository
import fr.outadoc.homeslide.common.rest.SimpleApiClientBuilder
import fr.outadoc.homeslide.hassapi.api.AuthApi
import fr.outadoc.homeslide.hassapi.api.DiscoveryApi
import fr.outadoc.homeslide.hassapi.api.HomeAssistantApi
import fr.outadoc.homeslide.hassapi.repository.DiscoveryRepository
import fr.outadoc.homeslide.logging.KLog
import fr.outadoc.homeslide.rest.ApiClientBuilder
import fr.outadoc.homeslide.rest.NetworkAccessManager
import fr.outadoc.homeslide.rest.baseurl.AltBaseUrlInterceptor
import fr.outadoc.homeslide.rest.baseurl.BaseUrlProvider
import fr.outadoc.homeslide.rest.baseurl.DefaultBaseUrlProvider
import fr.outadoc.homeslide.zeroconf.ZeroconfDiscoveryService
import fr.outadoc.mdi.AndroidMdiMapper
import fr.outadoc.mdi.common.MdiMapperLocator
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Suppress("unused")
class MainApplication : Application() {

    private val appModule = module {

        single {
            ChuckerInterceptor.Builder(get())
                .collector(ChuckerCollector(get()))
                .build()
        }

        single {
            ApiClientBuilder.newBuilder<HomeAssistantApi>(get(), get(), get())
                .addInterceptor(get<HttpLoggingInterceptor>())
                .addInterceptor(get<ChuckerInterceptor>())
                .build()
        }

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

        single<ZeroconfDiscoveryService<ZeroconfHost>> { HassZeroconfDiscoveryServiceImpl(get()) }
        single<DiscoveryRepository> { DiscoveryRepositoryImpl(get()) }
        single<IntentProvider> { AppIntentProvider() }

        single { PreferenceRepositoryImpl(get(), get()) }
        single<GlobalPreferenceRepository> { get<PreferenceRepositoryImpl>() }
        single<UrlPreferenceRepository> { get<PreferenceRepositoryImpl>() }
        single<TokenPreferenceRepository> { get<PreferenceRepositoryImpl>() }
        single<ThemePreferenceRepository> { get<PreferenceRepositoryImpl>() }
        single<ConsentPreferenceRepository> { get<PreferenceRepositoryImpl>() }
        single<PreferencePublisher> { get<PreferenceRepositoryImpl>() }

        single { ReviewManagerFactory.create(get()) }
        single { InAppReviewLaunchCounter(get()) }

        single<InAppReviewManager> { GoogleInAppReviewManager(get(), get()) }

        single { DefaultBaseUrlProvider(get(), get()) }
        single<BaseUrlProvider> { get<DefaultBaseUrlProvider>() }
        single<NetworkAccessManager> { get<DefaultBaseUrlProvider>() }

        viewModel { WelcomeViewModel() }
        viewModel {
            HostSetupViewModel(
                get(),
                get(),
                get(),
                get()
            )
        }
        viewModel { AuthCallbackViewModel(get(), get(), get()) }
        viewModel { ShortcutSetupViewModel() }
        viewModel {
            SuccessViewModel(
                get(),
                get()
            )
        }

        viewModel { EntityDetailViewModel() }
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            KLog.enableDebugLogging()
        }

        startKoin {
            KoinCustomLogger()
            androidContext(this@MainApplication)
            modules(systemModule() + commonModule() + controlProviderModule() + appModule)
        }

        MdiMapperLocator.instance = AndroidMdiMapper(applicationContext)
    }
}
