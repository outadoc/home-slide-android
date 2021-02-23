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

plugins {
    id(Dependencies.Android.library)
    kotlin(Dependencies.Kotlin.Plugin.android)
    id(Dependencies.AndroidX.Navigation.SafeArgs.plugin)
}

android {
    compileSdk = AppInfo.targetSdkVersion
    buildToolsVersion(Dependencies.Build.buildToolsVersion)

    defaultConfig {
        minSdk = LibraryInfo.minSdkVersion
        targetSdk = AppInfo.targetSdkVersion

        versionCode = LibraryInfo.defaultVersionCode
        versionName = LibraryInfo.defaultVersionName
    }

    buildTypes {
        named(BuildTypes.release) {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = Dependencies.Build.sourceCompatibility
        targetCompatibility = Dependencies.Build.sourceCompatibility
    }

    kotlinOptions {
        jvmTarget = Dependencies.Build.jvmTarget
        freeCompilerArgs = listOf(
            "-Xuse-experimental=kotlin.Experimental"
        )
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":lib-common-util"))
    implementation(project(":lib-zeroconf"))
    implementation(project(":lib-restclient"))
    implementation(project(":lib-homeassistant-api"))
    implementation(project(":lib-logging"))
    implementation(project(":lib-androidx-wearable"))

    implementation(Dependencies.Konfetti.core)
    implementation(Dependencies.Google.material)

    implementation(Dependencies.Kotlin.DateTime.core)

    // Data flow
    implementation(Dependencies.Uniflow.core)

    // AndroidX libs
    implementation(Dependencies.AndroidX.core)
    implementation(Dependencies.AndroidX.constraintLayout)
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.AndroidX.recyclerview)

    compileOnly(Dependencies.Google.Wearable.core)

    // Network libs
    implementation(Dependencies.Retrofit.core)
    implementation(Dependencies.OkHttp.logging)

    // Navigation
    implementation(Dependencies.AndroidX.Navigation.fragment)
    implementation(Dependencies.AndroidX.Navigation.ui)
    implementation(Dependencies.AndroidX.browser)

    // DI
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.viewModel)

    // AndroidX lifecycle
    implementation(Dependencies.AndroidX.Lifecycle.liveData)
    implementation(Dependencies.AndroidX.Lifecycle.viewModel)
}