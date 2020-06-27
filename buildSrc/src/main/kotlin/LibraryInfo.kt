object LibraryInfo {

    val minSdkVersion = minOf(
        AppInfo.Wear.minSdkVersion,
        AppInfo.Mobile.minSdkVersion
    )

    const val defaultVersionCode = 1
    const val defaultVersionName = "1.0"
}
