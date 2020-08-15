package com.google.androidx.wearable.intent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun Context.startRemoteActivity(intent: Intent): Int = suspendCoroutine { cont ->
    val resultReceiver = object : ResultReceiver(Handler(Looper.getMainLooper())) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            super.onReceiveResult(resultCode, resultData)
            cont.resume(resultCode)
        }
    }

    RemoteIntent.startRemoteActivity(this, intent, resultReceiver)
}
