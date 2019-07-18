package fr.outadoc.quickhass.feature.slideover.rest

import retrofit2.HttpException
import retrofit2.Response

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
