plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(AppInfo.targetSdkVersion)

    defaultConfig {
        minSdkVersion(AppInfo.libraryMinSdkVersion)
        targetSdkVersion(AppInfo.targetSdkVersion)
        consumerProguardFile("consumer-rules.pro")
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
    }
}

dependencies {
    // Kotlin runtime
    implementation(Dependencies.Kotlin.stdlib)

    implementation(Dependencies.AndroidX.core)
}