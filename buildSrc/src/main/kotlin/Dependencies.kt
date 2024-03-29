/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import org.gradle.api.JavaVersion

object Dependencies {

    object Build {
        const val jvmTarget = "1.8"
        val sourceCompatibility = JavaVersion.VERSION_1_8
    }

    object Android {
        const val classpath = "com.android.tools.build:gradle:4.2.1"
        const val application = "com.android.application"
        const val library = "com.android.library"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0"
        const val biometric = "androidx.biometric:biometric:1.1.0"
        const val browser = "androidx.browser:browser:1.3.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
        const val core = "androidx.core:core-ktx:1.5.0"
        const val preference = "androidx.preference:preference-ktx:1.1.1"
        const val recyclerview = "androidx.recyclerview:recyclerview:1.2.1"
        const val wear = "androidx.wear:wear:1.1.0"

        object Lifecycle {
            private const val version = "2.3.1"
            const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }

        object Navigation {
            private const val version = "2.3.5"
            const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            const val ui = "androidx.navigation:navigation-ui-ktx:$version"

            object SafeArgs {
                const val plugin = "androidx.navigation.safeargs.kotlin"
                const val classpath = "androidx.navigation:navigation-safe-args-gradle-plugin:$version"
            }
        }

        object Room {
            private const val version = "2.3.0"
            const val common = "androidx.room:room-ktx:$version"
            const val compiler = "androidx.room:room-compiler:$version"
        }
    }

    object Chucker {
        private const val version = "3.4.0"
        const val core = "com.github.ChuckerTeam.Chucker:library:$version"
        const val noop = "com.github.ChuckerTeam.Chucker:library-no-op:$version"
    }

    object Google {
        const val material = "com.google.android.material:material:1.3.0"

        object Firebase {
            object Crashlytics {
                const val core = "com.google.firebase:firebase-crashlytics-ktx:18.2.3"
                const val classpath = "com.google.firebase:firebase-crashlytics-gradle:2.7.1"
                const val plugin = "com.google.firebase.crashlytics"
            }
        }

        object Play {
            const val core = "com.google.android.play:core-ktx:1.8.1"
        }

        object PlayServices {
            const val classpath = "com.google.gms:google-services:4.3.8"
            const val plugin = "com.google.gms.google-services"
            const val wearable = "com.google.android.gms:play-services-wearable:17.1.0"
        }

        object Wearable {
            private const val version = "2.8.1"
            const val core = "com.google.android.wearable:wearable:$version"
        }
    }

    object Koin {
        private const val version = "3.1.2"
        const val android = "io.insert-koin:koin-core:$version"
        const val viewModel = "io.insert-koin:koin-android:$version"
    }

    object Konfetti {
        const val core = "nl.dionsegijn:konfetti:1.2.6"
    }

    object Kotlin {
        private const val version = "1.5.10"
        const val classpath = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"

        object Coroutines {
            private const val version = "1.5.0"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
            const val jdk9 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk9:$version"
        }

        object DateTime {
            private const val version = "0.2.1"
            const val core = "org.jetbrains.kotlinx:kotlinx-datetime:$version"
        }

        object Plugin {
            const val android = "android"
            const val kapt = "kapt"
            const val parcelize = "kotlin-parcelize"
            const val serialization = "plugin.serialization"
        }

        object Serialization {
            private const val version = "1.2.1"
            const val classpath = "org.jetbrains.kotlin:kotlin-serialization:${Kotlin.version}"
            const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"
        }
    }

    object LeakCanary {
        const val core = "com.squareup.leakcanary:leakcanary-android:2.7"
    }

    object Mdi {
        const val android = "fr.outadoc.mdi:mdi-android:6.2.95-b"
    }

    object OkHttp {
        const val logging = "com.squareup.okhttp3:logging-interceptor:4.9.1"
    }

    object Retrofit {
        private const val version = "2.9.0"
        const val core = "com.squareup.retrofit2:retrofit:$version"
        const val serializationConverter = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0"
    }

    object Skeleton {
        const val core = "com.faltenreich:skeletonlayout:4.0.0"
    }

    object Spotless {
        const val version = "5.11.0"
        const val plugin = "com.diffplug.spotless"
        const val ktlintVersion = "0.41.0"
    }

    object Test {
        const val junit = "org.junit.jupiter:junit-jupiter:5.7.0"
        const val mockito = "org.mockito:mockito-core:3.11.1"
    }

    object Tools {
        const val desugaring = "com.android.tools:desugar_jdk_libs:1.1.5"
    }

    object Timber {
        const val core = "com.jakewharton.timber:timber:4.7.1"
    }

    object Uniflow {
        const val core = "org.uniflow-kt:uniflow-android:1.0.10"
    }
}
