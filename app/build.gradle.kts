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
    id(Dependencies.Android.application)
    id(Dependencies.Google.PlayServices.plugin)
    id(Dependencies.Google.Firebase.Crashlytics.plugin)
    kotlin(Dependencies.Kotlin.Plugin.android)
}

android {
    compileSdk = AppInfo.targetSdkVersion
    buildToolsVersion(Dependencies.Build.buildToolsVersion)

    defaultConfig {
        applicationId = AppInfo.applicationId

        targetSdk = AppInfo.targetSdkVersion
        minSdk = AppInfo.Mobile.minSdkVersion

        versionCode = AppInfo.Mobile.versionCode
        versionName = AppInfo.versionName
    }

    buildTypes {
        val fieldEnableDetails = "ENABLE_DETAILS"

        named(BuildTypes.debug) {
            applicationIdSuffix = AppInfo.applicationIdSuffix
            buildConfigField("boolean", fieldEnableDetails, "false")
        }

        named(BuildTypes.release) {
            isMinifyEnabled = true
            buildConfigField("boolean", fieldEnableDetails, "false")
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

    implementation(project(":common"))
    implementation(project(":feature-onboarding"))
    implementation(project(":feature-controlprovider"))

    implementation(Dependencies.Kotlin.Coroutines.core)
    implementation(Dependencies.Kotlin.Coroutines.android)
    implementation(Dependencies.Kotlin.DateTime.core)

    // UI libs
    implementation(Dependencies.Google.material)
    implementation(Dependencies.Skeleton.core)
    implementation(Dependencies.Mdi.android)

    // Network libs
    implementation(Dependencies.Retrofit.core)
    implementation(Dependencies.OkHttp.logging)

    // AndroidX libs
    implementation(Dependencies.AndroidX.core)
    implementation(Dependencies.AndroidX.constraintLayout)
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.AndroidX.biometric)
    implementation(Dependencies.AndroidX.browser)

    // AndroidX lifecycle
    implementation(Dependencies.AndroidX.Lifecycle.liveData)
    implementation(Dependencies.AndroidX.Lifecycle.viewModel)

    // Data flow
    implementation(Dependencies.Uniflow.core)

    // Navigation
    implementation(Dependencies.AndroidX.Navigation.fragment)
    implementation(Dependencies.AndroidX.Navigation.ui)

    // Preferences & persistence
    implementation(Dependencies.AndroidX.preference)
    implementation(Dependencies.AndroidX.Room.common)

    // DI
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.viewModel)

    // Firebase
    implementation(Dependencies.Google.Firebase.Crashlytics.core)

    implementation(Dependencies.Google.Play.core)

    // Tools
    debugImplementation(Dependencies.LeakCanary.core)
    debugImplementation(Dependencies.Chucker.core)
    releaseImplementation(Dependencies.Chucker.noop)

    // Testing
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.mockito)

    coreLibraryDesugaring(Dependencies.Tools.desugaring)
}