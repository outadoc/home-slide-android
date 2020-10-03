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

        versionCode = 2
        versionName = "5.5.55"
    }

    buildTypes {
        named(BuildTypes.release) {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = Dependencies.Build.sourceCompatibility
        targetCompatibility = Dependencies.Build.sourceCompatibility
    }

    kotlinOptions {
        jvmTarget = Dependencies.Build.jvmTarget
    }
}

dependencies {
    implementation(Dependencies.AndroidX.appcompat)
}