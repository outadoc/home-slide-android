plugins {
    id("com.android.library")
    kotlin("android")
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
    implementation(project(":lib-restclient"))
    implementation(project(":lib-homeassistant-api"))
    implementation(project(":lib-logging"))

    // Kotlin runtime
    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.Kotlin.Coroutines.core)
    implementation(Dependencies.Kotlin.Coroutines.jdk9)
    implementation(Dependencies.Kotlin.Coroutines.android)

    // AndroidX libs
    implementation(Dependencies.AndroidX.core)

    // DI
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.viewModel)

    // AndroidX lifecycle
    implementation(Dependencies.AndroidX.Lifecycle.extensions)
    implementation(Dependencies.AndroidX.Lifecycle.liveData)
    implementation(Dependencies.AndroidX.Lifecycle.viewModel)
}