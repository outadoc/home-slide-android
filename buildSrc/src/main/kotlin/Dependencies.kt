import org.gradle.api.JavaVersion

object Dependencies {

    object Build {
        const val buildToolsVersion = "30.0.1"
        const val jvmTarget = "1.8"
        val sourceCompatibility = JavaVersion.VERSION_1_8
    }

    object Android {
        const val classpath = "com.android.tools.build:gradle:4.1.0-rc02"
        const val application = "com.android.application"
        const val library = "com.android.library"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0-alpha01"
        const val biometric = "androidx.biometric:biometric:1.0.1"
        const val browser = "androidx.browser:browser:1.3.0-alpha01"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
        const val core = "androidx.core:core-ktx:1.3.0"
        const val preference = "androidx.preference:preference-ktx:1.1.1"
        const val recyclerview = "androidx.recyclerview:recyclerview:1.1.0"
        const val wear = "androidx.wear:wear:1.1.0-rc02"

        object Lifecycle {
            private const val version = "2.2.0"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:$version"
            const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }

        object Navigation {
            private const val version = "2.2.2"
            const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            const val ui = "androidx.navigation:navigation-ui-ktx:$version"

            object SafeArgs {
                const val plugin = "androidx.navigation.safeargs.kotlin"
                const val classpath = "androidx.navigation:navigation-safe-args-gradle-plugin:$version"
            }
        }

        object Room {
            private const val version = "2.2.5"
            const val common = "androidx.room:room-ktx:$version"
            const val compiler = "androidx.room:room-compiler:$version"
        }
    }

    object Chucker {
        private const val version = "3.2.0"
        const val core = "com.github.ChuckerTeam.Chucker:library:$version"
        const val noop = "com.github.ChuckerTeam.Chucker:library-no-op:$version"
    }

    object Google {
        const val material = "com.google.android.material:material:1.3.0-alpha02"

        object PlayServices {
            const val wearable = "com.google.android.gms:play-services-wearable:17.0.0"
        }

        object Wearable {
            private const val version = "2.7.0"
            const val core = "com.google.android.wearable:wearable:$version"
        }
    }

    object Koin {
        private const val version = "2.1.6"
        const val android = "org.koin:koin-core:$version"
        const val viewModel = "org.koin:koin-androidx-viewmodel:$version"
    }

    object Konfetti {
        const val core = "nl.dionsegijn:konfetti:1.1.3"
    }

    object Kotlin {
        private const val version = "1.4.0"
        const val classpath = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"

        object Coroutines {
            private const val version = "1.3.8"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
            const val jdk9 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk9:$version"
        }

        object Plugin {
            const val android = "android"
            const val androidExtensions = "android.extensions"
            const val kapt = "kapt"
        }
    }

    object LeakCanary {
        const val core = "com.squareup.leakcanary:leakcanary-android:2.4"
    }

    object Moshi {
        private const val version = "1.9.2"
        const val core = "com.squareup.moshi:moshi:$version"
        const val codegen = "com.squareup.moshi:moshi-kotlin-codegen:$version"
    }

    object OkHttp {
        const val logging = "com.squareup.okhttp3:logging-interceptor:4.6.0"
    }

    object Retrofit {
        private const val version = "2.8.1"
        const val core = "com.squareup.retrofit2:retrofit:$version"
        const val moshiConverter = "com.squareup.retrofit2:converter-moshi:$version"
    }

    object Skeleton {
        const val core = "com.faltenreich:skeletonlayout:2.0.0"
    }

    object Spotless {
        const val version = "3.27.1"
        const val plugin = "com.diffplug.gradle.spotless"
    }

    object Test {
        const val junit = "org.junit.jupiter:junit-jupiter:5.6.0"
        const val mockito = "org.mockito:mockito-core:3.2.4"
    }

    object Tools {
        const val desugaring = "com.android.tools:desugar_jdk_libs:1.0.9"
    }

    object Timber {
        const val core = "com.jakewharton.timber:timber:4.7.1"
    }

    object Uniflow {
        const val core = "io.uniflow:uniflow-androidx:0.11.1"
    }
}
