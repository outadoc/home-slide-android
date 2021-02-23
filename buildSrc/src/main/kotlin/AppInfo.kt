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

@Suppress("MemberVisibilityCanBePrivate")
object AppInfo {

    const val versionName = "1.6.1"
    const val baseVersionCode = 163

    const val targetSdkVersion = 30

    const val applicationId = "fr.outadoc.quickhass"
    const val applicationIdSuffix = ".debug"

    object Mobile {
        val versionCode = getVersionCode(variant = 0)
        const val minSdkVersion = 21
    }

    object Wear {
        val versionCode = getVersionCode(variant = 1)
        const val minSdkVersion = 25
    }

    /**
     *  Computes the app's version code.
     *
     *  Output format:
     *  aabbbcc
     *
     *  aa          the targetSdkVersion
     *    bb        base version code (see [baseVersionCode])
     *      cc      multi-APK variant
     */
    private fun getVersionCode(variant: Int): Int {
        check(baseVersionCode in 100..999)
        check(variant in 0..99)

        val versionStr = baseVersionCode.toString()
        val variantStr = variant.toString().padStart(2, '0')
        val targetVersionStr = targetSdkVersion.toString()
        return "$targetVersionStr$versionStr$variantStr".toInt()
    }
}
