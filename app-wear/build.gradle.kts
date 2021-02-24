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
    androidApp()
    playServices()
    crashlytics()
    kotlinAndroid()
}

android {
    compileSdk = AppInfo.targetSdkVersion
    buildToolsVersion(Dependencies.Build.buildToolsVersion)

    defaultConfig {
        applicationId = AppInfo.applicationId

        targetSdk = AppInfo.targetSdkVersion
        minSdk = AppInfo.Wear.minSdkVersion

        versionCode = AppInfo.Wear.versionCode
        versionName = AppInfo.versionName
    }

    buildTypes {
        named(BuildTypes.debug) {
            applicationIdSuffix = AppInfo.applicationIdSuffix
        }

        named(BuildTypes.release) {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = Dependencies.Build.sourceCompatibility
        targetCompatibility = Dependencies.Build.sourceCompatibility
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = Dependencies.Build.jvmTarget
        freeCompilerArgs = listOf(
            "-Xuse-experimental=kotlin.Experimental"
        )
    }
}

dependencies {
    implementation(project(":lib-zeroconf"))
    implementation(project(":lib-restclient"))
    implementation(project(":lib-homeassistant-api"))
    implementation(project(":lib-common-util"))
    implementation(project(":lib-logging"))
    implementation(project(":lib-androidx-wearable"))

    implementation(project(":common"))

    implementation(Dependencies.Kotlin.Coroutines.core)
    implementation(Dependencies.Kotlin.Coroutines.android)
    implementation(Dependencies.Kotlin.DateTime.core)

    // UI libs
    implementation(Dependencies.Google.material)
    implementation(Dependencies.Mdi.android)

    // AndroidX libs
    implementation(Dependencies.AndroidX.core)
    implementation(Dependencies.AndroidX.constraintLayout)
    implementation(Dependencies.AndroidX.appcompat)

    // Network libs
    implementation(Dependencies.Retrofit.core)
    implementation(Dependencies.OkHttp.logging)

    // AndroidX lifecycle
    implementation(Dependencies.AndroidX.Lifecycle.liveData)
    implementation(Dependencies.AndroidX.Lifecycle.viewModel)

    // Data flow
    implementation(Dependencies.Uniflow.core)

    // Firebase
    implementation(Dependencies.Google.Firebase.Crashlytics.core)

    // Preferences
    implementation(Dependencies.AndroidX.preference)

    // Wear OS
    implementation(Dependencies.Google.PlayServices.wearable)

    // DI
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.viewModel)

    implementation(Dependencies.AndroidX.wear)
    compileOnly(Dependencies.Google.Wearable.core)

    coreLibraryDesugaring(Dependencies.Tools.desugaring)
}
