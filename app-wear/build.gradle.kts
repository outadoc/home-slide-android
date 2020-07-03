plugins {
    id(Dependencies.Android.application)
    kotlin(Dependencies.Kotlin.Plugin.android)
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
        named("debug") {
            applicationIdSuffix = AppInfo.applicationIdSuffix
        }

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
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = Dependencies.Build.jvmTarget
        freeCompilerArgs = listOf(
            "-Xallow-result-return-type",
            "-Xuse-experimental=kotlin.Experimental"
        )
    }
}

dependencies {
    implementation(project(":lib-mdi"))
    implementation(project(":lib-zeroconf"))
    implementation(project(":lib-restclient"))
    implementation(project(":lib-homeassistant-api"))
    implementation(project(":lib-common-util"))
    implementation(project(":lib-logging"))

    implementation(project(":common"))

    // Kotlin runtime
    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.Kotlin.Coroutines.core)
    implementation(Dependencies.Kotlin.Coroutines.android)

    // AndroidX libs
    implementation(Dependencies.AndroidX.core)
    implementation(Dependencies.AndroidX.constraintLayout)
    implementation(Dependencies.AndroidX.appcompat)

    // Network libs
    implementation(Dependencies.Retrofit.core)
    implementation(Dependencies.OkHttp.logging)

    // AndroidX lifecycle
    implementation(Dependencies.AndroidX.Lifecycle.extensions)
    implementation(Dependencies.AndroidX.Lifecycle.liveData)
    implementation(Dependencies.AndroidX.Lifecycle.viewModel)

    // Data flow
    implementation(Dependencies.Uniflow.core)

    // Preferences
    implementation(Dependencies.AndroidX.preference)

    // Wear OS
    implementation(Dependencies.Google.PlayServices.wearable)

    // DI
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.viewModel)

    implementation(Dependencies.AndroidX.wear)
    implementation(Dependencies.Google.Wearable.support)
    compileOnly(Dependencies.Google.Wearable.core)

    coreLibraryDesugaring(Dependencies.Tools.desugaring)
}
