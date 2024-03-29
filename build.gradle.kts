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

plugins {
    id(Dependencies.Spotless.plugin) version Dependencies.Spotless.version
}

buildscript {
    repositories {
        google()
        mavenCentral()
        kotlinx()
    }

    dependencies {
        classpath(Dependencies.Kotlin.classpath)
        classpath(Dependencies.Android.classpath)
        classpath(Dependencies.Google.PlayServices.classpath)
        classpath(Dependencies.Google.Firebase.Crashlytics.classpath)
        classpath(Dependencies.AndroidX.Navigation.SafeArgs.classpath)
        classpath(Dependencies.Kotlin.Serialization.classpath)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        kotlinx()
        jitpack()

        val githubPackagesToken: String? by project
        maven(url = "https://maven.pkg.github.com/outadoc/mdi-android") {
            credentials {
                username = "token"
                password = System.getenv("GITHUB_PACKAGES_TOKEN")
                    ?.takeIf { it.isNotBlank() }
                    ?: githubPackagesToken
            }
        }
    }
}

spotless {
    format("misc") {
        target("**/*.md", "**/.gitignore", "**/.xml")
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    kotlin {
        target("**/*.kt")
        ktlint(Dependencies.Spotless.ktlintVersion)
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint(Dependencies.Spotless.ktlintVersion)
    }
}
