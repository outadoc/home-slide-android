plugins {
    id(Dependencies.Android.library)
    kotlin(Dependencies.Kotlin.Plugin.android)
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
        compose = true
    }

    compileOptions {
        sourceCompatibility = Dependencies.Build.sourceCompatibility
        targetCompatibility = Dependencies.Build.sourceCompatibility
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        useIR = true
        jvmTarget = Dependencies.Build.jvmTarget
        freeCompilerArgs = listOf(
            "-Xallow-jvm-ir-dependencies", "-Xskip-prerelease-check"
        )
    }

    composeOptions {
        kotlinCompilerVersion = Dependencies.Kotlin.version
        kotlinCompilerExtensionVersion = "1.0.0-alpha02"
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":lib-common-util"))
    implementation(project(":lib-restclient"))
    implementation(project(":lib-homeassistant-api"))
    implementation(project(":lib-logging"))

    // Data flow
    implementation(Dependencies.Uniflow.core)

    // AndroidX libs
    implementation(Dependencies.AndroidX.core)

    // Navigation
    implementation(Dependencies.AndroidX.Navigation.fragment)
    implementation(Dependencies.AndroidX.Navigation.ui)

    // DI
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.viewModel)

    // AndroidX lifecycle
    implementation(Dependencies.AndroidX.Lifecycle.extensions)
    implementation(Dependencies.AndroidX.Lifecycle.liveData)
    implementation(Dependencies.AndroidX.Lifecycle.viewModel)
    
    // Compose
    implementation("androidx.compose.ui:ui:1.0.0-alpha02")
    // Tooling support (Previews, etc.)
    implementation("androidx.ui:ui-tooling:1.0.0-alpha02")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:1.0.0-alpha02")
    // Material Design
    implementation("androidx.compose.material:material:1.0.0-alpha02")
    // Integration with observables
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0-alpha02")

    coreLibraryDesugaring(Dependencies.Tools.desugaring)
}