plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdkVersion(AppInfo.targetSdkVersion)

    defaultConfig {
        minSdkVersion(AppInfo.libraryMinSdkVersion)
        targetSdkVersion(AppInfo.targetSdkVersion)
        consumerProguardFiles("consumer-rules.pro")
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
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
    implementation(project(":lib-common-util"))
    implementation(project(":common"))
    implementation(project(":lib-zeroconf"))
    implementation(project(":lib-restclient"))
    implementation(project(":lib-homeassistant-api"))

    implementation(Dependencies.Konfetti.core)

    // Kotlin runtime
    implementation(Dependencies.Kotlin.stdlib)

    // AndroidX libs
    implementation(Dependencies.AndroidX.core)
    implementation(Dependencies.AndroidX.constraintLayout)
    implementation(Dependencies.AndroidX.appcompat)

    // Network libs
    implementation(Dependencies.Retrofit.core)
    implementation(Dependencies.OkHttp.logging)
    implementation(Dependencies.Moshi.core)
    kapt(Dependencies.Moshi.codegen)

    // Navigation
    implementation(Dependencies.AndroidX.Navigation.fragment)
    implementation(Dependencies.AndroidX.Navigation.ui)
    implementation(Dependencies.AndroidX.browser)

    // DI
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.viewModel)

    // Logging
    implementation(Dependencies.Timber.core)
    implementation(Dependencies.Timber.ktx)

    // AndroidX lifecycle
    implementation(Dependencies.AndroidX.Lifecycle.extensions)
    implementation(Dependencies.AndroidX.Lifecycle.liveData)
    implementation(Dependencies.AndroidX.Lifecycle.viewModel)

    debugImplementation(Dependencies.Chucker.core)
    releaseImplementation(Dependencies.Chucker.noop)
}