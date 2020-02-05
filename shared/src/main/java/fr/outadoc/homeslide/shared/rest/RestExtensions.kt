package fr.outadoc.homeslide.shared.rest

import okhttp3.HttpUrl
import retrofit2.HttpException
import retrofit2.Response
import java.net.MalformedURLException

suspend fun <T> wrapResponse(apiCall: suspend () -> Response<T>): Result<T> {
    return try {
        val res = apiCall()

        if (res.isSuccessful && res.body() != null) {
            Result.success(res.body()!!)
        } else {
            Result.failure(HttpException(res))
        }

    } catch (e: Exception) {
        Result.failure(e)
    }
}

fun String.toUrl(): HttpUrl {
    return HttpUrl.parse(this) ?: throw MalformedURLException()
}

fun String?.toUrlOrNull(): HttpUrl? {
    if (this == null) return null
    return HttpUrl.parse(this)
}