@Suppress("MemberVisibilityCanBePrivate")
object AppInfo {

    const val targetSdkVersion = 30
    const val versionName = "1.3"

    const val applicationId = "fr.outadoc.quickhass"
    const val applicationIdSuffix = ".debug"

    object Mobile {
        val versionCode = getVersionCode(0)
        const val minSdkVersion = 21
    }

    object Wear {
        val versionCode = getVersionCode(1)
        const val minSdkVersion = 25
    }

    /**
     *  --- Versioning scheme ---
     *  first two digits: targetSdkVersion
     *  next three digits: versionName
     *  last two digits: multi-APK variant
     */
    private fun getVersionCode(variant: Int): Int {
        val versionStr = versionName
            .replace(".", "")
            .also {
                assert(it.length <= 3) {
                    "versionName must respect format x.y.z"
                }
            }
            .replace(".", "")
            .padEnd(3, '0')

        val variantStr = variant.toString().padStart(2, '0')
        val targetVersionStr = targetSdkVersion.toString()

        return "${targetVersionStr}${versionStr}$variantStr".toInt()
    }
}
