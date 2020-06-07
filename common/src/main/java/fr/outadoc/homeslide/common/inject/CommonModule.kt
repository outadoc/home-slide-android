package fr.outadoc.homeslide.common.inject

import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.d
import com.squareup.moshi.Moshi
import fr.outadoc.homeslide.common.feature.auth.repository.AppOAuthConfiguration
import fr.outadoc.homeslide.common.feature.auth.repository.AuthRepositoryImpl
import fr.outadoc.homeslide.common.feature.grid.vm.EntityGridViewModel
import fr.outadoc.homeslide.common.json.SkipBadElementsListAdapter
import fr.outadoc.homeslide.common.preferences.BaseUrlConfigProviderImpl
import fr.outadoc.homeslide.common.rest.TokenProviderImpl
import fr.outadoc.homeslide.hassapi.api.HomeAssistantApi
import fr.outadoc.homeslide.hassapi.factory.TileFactory
import fr.outadoc.homeslide.hassapi.factory.TileFactoryImpl
import fr.outadoc.homeslide.hassapi.repository.AuthRepository
import fr.outadoc.homeslide.rest.ApiClientBuilder
import fr.outadoc.homeslide.rest.auth.AccessTokenProvider
import fr.outadoc.homeslide.rest.auth.OAuthConfiguration
import fr.outadoc.homeslide.rest.baseurl.BaseUrlConfigProvider
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.converter.moshi.MoshiConverterFactory

fun commonModule() = module {
    single<BaseUrlConfigProvider> { BaseUrlConfigProviderImpl(get()) }
    single<AccessTokenProvider> { TokenProviderImpl(get(), get()) }
    single<OAuthConfiguration> { AppOAuthConfiguration() }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }

    single<TileFactory> { TileFactoryImpl(get()) }

    single {
        HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.tag("OkHttp").d { message }
            }
        }).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        Moshi.Builder()
            .add(SkipBadElementsListAdapter.newFactory())
            .build()
    }

    single<Converter.Factory> { MoshiConverterFactory.create(get()) }

    viewModel { EntityGridViewModel(get(), get()) }
}