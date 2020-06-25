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
    implementation(project(":lib-common-util"))
    implementation(project(":lib-mdi"))

    // Kotlin runtime
    implementation(Dependencies.Kotlin.stdlib)

    implementation(Dependencies.AndroidX.core)
    implementation(Dependencies.AndroidX.appcompat)

    implementation(Dependencies.AndroidX.Room.common)

    // Network libs
    implementation(Dependencies.Retrofit.core)
    implementation(Dependencies.Moshi.core)
    kapt(Dependencies.Moshi.codegen)

    // Logging
    implementation(Dependencies.Timber.core)
    implementation(Dependencies.Timber.ktx)

    // Testing
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.mockito)
}