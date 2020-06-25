plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(AppInfo.targetSdkVersion)
    buildToolsVersion(Dependencies.Build.buildToolsVersion)

    defaultConfig {
        applicationId = AppInfo.applicationId
        targetSdkVersion(AppInfo.targetSdkVersion)
        minSdkVersion(AppInfo.Mobile.minSdkVersion)
        versionCode = AppInfo.Mobile.versionCode
        versionName = AppInfo.versionName
    }

    buildTypes {
        val fieldEnableDetails = "ENABLE_DETAILS"

        named("debug") {
            applicationIdSuffix = AppInfo.applicationIdSuffix
            buildConfigField("boolean", fieldEnableDetails, "false")
        }

        named("release") {
            isMinifyEnabled = false
            buildConfigField("boolean", fieldEnableDetails, "false")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility(Dependencies.Build.sourceCompatibility)
        targetCompatibility(Dependencies.Build.sourceCompatibility)
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
    implementation(project(":feature-onboarding"))
    implementation(project(":feature-controlprovider"))

    // Kotlin runtime
    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.Kotlin.Coroutines.core)
    implementation(Dependencies.Kotlin.Coroutines.android)

    // UI libs
    implementation(Dependencies.Google.material)
    implementation(Dependencies.Skeleton.core)

    // Network libs
    implementation(Dependencies.OkHttp.logging)

    // AndroidX libs
    implementation(Dependencies.AndroidX.core)
    implementation(Dependencies.AndroidX.constraintLayout)
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.AndroidX.biometric)

    // AndroidX lifecycle
    implementation(Dependencies.AndroidX.Lifecycle.extensions)
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

    // Tools
    debugImplementation(Dependencies.LeakCanary.core)
    debugImplementation(Dependencies.Chucker.core)
    releaseImplementation(Dependencies.Chucker.noop)

    // Testing
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.mockito)
}