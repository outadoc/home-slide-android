plugins {
    id(Dependencies.Android.library)
    kotlin(Dependencies.Kotlin.Plugin.android)
    kotlin(Dependencies.Kotlin.Plugin.kapt)
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

    kapt {
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
        }
    }

    compileOptions {
        sourceCompatibility = Dependencies.Build.sourceCompatibility
        targetCompatibility = Dependencies.Build.sourceCompatibility
    }

    kotlinOptions {
        jvmTarget = Dependencies.Build.jvmTarget
        freeCompilerArgs = listOf(
            "-Xuse-experimental=kotlin.Experimental",
            "-Xuse-experimental=kotlin.time.ExperimentalTime"
        )
    }
}

dependencies {
    implementation(project(":lib-mdi"))
    implementation(project(":lib-restclient"))
    implementation(project(":lib-homeassistant-api"))
    implementation(project(":lib-common-util"))
    implementation(project(":lib-logging"))

    implementation(Dependencies.Kotlin.Coroutines.core)
    implementation(Dependencies.Kotlin.Coroutines.android)

    // Wear OS
    implementation(Dependencies.Google.PlayServices.wearable)

    implementation(Dependencies.Google.material)

    implementation(Dependencies.AndroidX.core)
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.Kotlin.DateTime.core)
    implementation(Dependencies.AndroidX.preference)

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
    implementation(Dependencies.OkHttp.logging)

    // Firebase
    implementation(Dependencies.Google.Firebase.Crashlytics.core)

    // DI
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.viewModel)

    // Testing
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.mockito)
}