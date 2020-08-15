package com.google.androidx.wearable.intent

import android.os.Parcel

internal fun <T> Parcel.use(block: Parcel.() -> T): T {
    val res = block()
    recycle()
    return res
}
