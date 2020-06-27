// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id(Dependencies.Spotless.plugin) version Dependencies.Spotless.version
}

buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath(Dependencies.Kotlin.classpath)
        classpath(Dependencies.Android.classpath)
        classpath(Dependencies.AndroidX.Navigation.SafeArgs.classpath)
    }
}

allprojects {
    repositories {
        google()
        jcenter()

        // Required for Chucker
        maven(url = "https://jitpack.io")
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
        ktlint()
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}
