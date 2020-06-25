plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
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

    kapt {
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
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
    implementation(project(":lib-mdi"))
    implementation(project(":lib-restclient"))
    implementation(project(":lib-homeassistant-api"))
    implementation(project(":lib-common-util"))

    // Kotlin runtime
    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.Kotlin.Coroutines.core)
    implementation(Dependencies.Kotlin.Coroutines.android)

    // Wear OS
    implementation(Dependencies.Google.PlayServices.wearable)

    implementation(Dependencies.Google.material)

    implementation(Dependencies.AndroidX.core)
    implementation(Dependencies.AndroidX.appcompat)

    // AndroidX lifecycle
    implementation(Dependencies.AndroidX.Lifecycle.extensions)
    implementation(Dependencies.AndroidX.Lifecycle.liveData)
    implementation(Dependencies.AndroidX.Lifecycle.viewModel)

    // Data flow
    implementation(Dependencies.Uniflow.core)

    // Network libs
    implementation(Dependencies.Retrofit.core)
    implementation(Dependencies.Retrofit.moshiConverter)
    implementation(Dependencies.Moshi.core)
    kapt(Dependencies.Moshi.codegen)

    // Persistence
    implementation(Dependencies.AndroidX.Room.common)
    kapt(Dependencies.AndroidX.Room.compiler)

    // Logging
    implementation(Dependencies.Timber.core)
    implementation(Dependencies.Timber.ktx)
    implementation(Dependencies.OkHttp.logging)

    // DI
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.viewModel)

    // Testing
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.mockito)
}