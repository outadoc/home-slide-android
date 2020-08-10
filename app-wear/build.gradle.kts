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

    signingConfigs {
        create(BuildTypes.release) {
            isV1SigningEnabled = false
            isV2SigningEnabled = true

            val keyStorePassword: String by project

            storeFile(file(SigningConfig.keyStorePath))
            storePassword(keyStorePassword)
            keyAlias(SigningConfig.keyAlias)
            keyPassword(keyStorePassword)
        }
    }

    buildTypes {
        named(BuildTypes.debug) {
            applicationIdSuffix = AppInfo.applicationIdSuffix
        }

        named(BuildTypes.release) {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName(BuildTypes.release)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
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
    implementation(project(":lib-mdi"))
    implementation(project(":lib-zeroconf"))
    implementation(project(":lib-restclient"))
    implementation(project(":lib-homeassistant-api"))
    implementation(project(":lib-common-util"))
    implementation(project(":lib-logging"))

    implementation(project(":common"))

    implementation(Dependencies.Kotlin.Coroutines.core)
    implementation(Dependencies.Kotlin.Coroutines.android)

    // UI libs
    implementation(Dependencies.Google.material)

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
