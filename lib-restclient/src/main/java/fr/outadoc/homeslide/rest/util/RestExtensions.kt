package fr.outadoc.homeslide.rest.util

import java.net.MalformedURLException
import okhttp3.HttpUrl
import retrofit2.HttpException
import retrofit2.Response

fun <T> Response<T>.getResponseOrThrow(): T {
    return if (isSuccessful && body() != null) {
        body()!!
    } else {
        throw HttpException(this)
    }
}

fun String.toUrl(): HttpUrl {
    return HttpUrl.parse(this) ?: throw MalformedURLException()
}

fun String?.toUrlOrNull(): HttpUrl? {
    if (this == null) return null
    return HttpUrl.parse(this)
}
