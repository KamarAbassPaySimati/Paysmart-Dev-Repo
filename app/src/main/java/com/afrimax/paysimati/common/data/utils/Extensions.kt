package com.afrimax.paysimati.common.data.utils


import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_CONFLICT
import java.net.HttpURLConnection.HTTP_CREATED
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR
import java.net.HttpURLConnection.HTTP_OK
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Safely executes an API call and handles various exceptions and response codes,
 * returning a `GenericResult`.
 *
 * @param apiCall A lambda function representing the API call to be performed.
 * @return A `GenericResult` containing either the successful result of the API call
 * or an error indicating the type of network or response error that occurred.
 */
inline fun <T> safeApiCall(
    apiCall: () -> Response<T>
): GenericResult<T, Errors.Network> {
    try {
        val response = apiCall()
        val body = response.body()

        return when (response.code()) {
            HTTP_OK, HTTP_CREATED -> if (body != null) GenericResult.Success(body) else GenericResult.Error(
                Errors.Network.NO_RESPONSE
            )

            HTTP_INTERNAL_ERROR -> GenericResult.Error(Errors.Network.INTERNAL_ERROR)
            HTTP_UNAUTHORIZED -> GenericResult.Error(Errors.Network.UNAUTHORIZED)
            HTTP_BAD_REQUEST -> GenericResult.Error(Errors.Network.BAD_REQUEST)
            HTTP_CONFLICT -> GenericResult.Error(Errors.Network.CONFLICT_FOUND)
            else -> GenericResult.Error(Errors.Network.UNKNOWN)
        }

    } catch (e: UnknownHostException) {
        return GenericResult.Error(Errors.Network.NO_INTERNET)
    } catch (e: SocketTimeoutException) {
        return GenericResult.Error(Errors.Network.REQUEST_TIMEOUT)
    } catch (e: Exception) {
        return GenericResult.Error(Errors.Network.UNKNOWN)
    }
}