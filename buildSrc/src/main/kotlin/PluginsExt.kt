import org.gradle.kotlin.dsl.PluginDependenciesSpecScope
import org.gradle.kotlin.dsl.kotlin

/*
 * Copyright 2021 Baptiste Candellier
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

fun PluginDependenciesSpecScope.androidLibrary() {
    id("com.android.library")
}

fun PluginDependenciesSpecScope.androidApp() {
    id("com.android.application")
}

fun PluginDependenciesSpecScope.kotlinAndroid() {
    kotlin("android")
}

fun PluginDependenciesSpecScope.kotlinSerialization() {
    id("plugin.serialization")
}

fun PluginDependenciesSpecScope.kotlinParcelize() {
    id("kotlin-parcelize")
}

fun PluginDependenciesSpecScope.playServices() {
    id("com.google.gms.google-services")
}

fun PluginDependenciesSpecScope.crashlytics() {
    id("com.google.firebase.crashlytics")
}

fun PluginDependenciesSpecScope.kapt() {
    kotlin("kapt")
}

fun PluginDependenciesSpecScope.spotless() {
    id("com.diffplug.gradle.spotless")
}