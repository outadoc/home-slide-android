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

    compileOptions {
        sourceCompatibility = Dependencies.Build.sourceCompatibility
        targetCompatibility = Dependencies.Build.sourceCompatibility
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        useIR = true
        jvmTarget = Dependencies.Build.jvmTarget
        freeCompilerArgs = listOf(
            "-Xuse-experimental=kotlin.Experimental"
        )
    }
}

dependencies {
    implementation(project(":lib-restclient"))
    implementation(project(":lib-homeassistant-api"))
    implementation(project(":lib-logging"))

    implementation(Dependencies.Kotlin.Coroutines.core)
    implementation(Dependencies.Kotlin.Coroutines.jdk9)
    implementation(Dependencies.Kotlin.Coroutines.android)

    // Network libs
    implementation(Dependencies.Retrofit.core)

    // AndroidX libs
    implementation(Dependencies.AndroidX.core)

    // DI
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.viewModel)

    // AndroidX lifecycle
    implementation(Dependencies.AndroidX.Lifecycle.extensions)
    implementation(Dependencies.AndroidX.Lifecycle.liveData)
    implementation(Dependencies.AndroidX.Lifecycle.viewModel)

    coreLibraryDesugaring(Dependencies.Tools.desugaring)
}