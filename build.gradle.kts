// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("com.diffplug.gradle.spotless") version Dependencies.spotlessPluginVersion
}

buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath(Dependencies.Build.gradle)
        classpath(Dependencies.Kotlin.plugin)
        classpath(Dependencies.AndroidX.Navigation.safeArgs)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint()
    }
}