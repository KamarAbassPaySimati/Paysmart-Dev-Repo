package com.afrimax.paysimati.common.data.utils


import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.common.domain.utils.Issues.Network
import com.afrimax.paysimati.common.domain.utils.Result
import com.afrimax.paysimati.data.model.DefaultResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_CONFLICT
import java.net.HttpURLConnection.HTTP_CREATED
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR
import java.net.HttpURLConnection.HTTP_NO_CONTENT
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
            HTTP_OK, HTTP_CREATED, HTTP_NO_CONTENT -> if (body != null) GenericResult.Success(body) else GenericResult.Error(
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
        e.printStackTrace()
        return GenericResult.Error(Errors.Network.UNKNOWN)
    }
}

inline fun <T> safeApiCall2(
    apiCall: () -> Response<T>
): Result<T, Network> {
    try {
        val response = apiCall()
        val body = response.body()
        val errorBody = response.errorBody()?.string()

        return when (response.code()) {
            HTTP_OK, HTTP_CREATED, HTTP_NO_CONTENT -> if (body != null) Result.Success(body) else Result.Error(Network.NoResponse(""))

            HTTP_INTERNAL_ERROR -> Result.Error(Network.InternalError(errorBody.parseErrorMessage()))

            HTTP_UNAUTHORIZED -> Result.Error(Network.UnAuthorized(errorBody.parseErrorMessage()))
            HTTP_BAD_REQUEST -> Result.Error(Network.BadRequest(errorBody.parseErrorMessage()))
            HTTP_CONFLICT -> Result.Error(Network.ConflictFound(errorBody.parseErrorMessage()))
            else -> Result.Error(Network.Unknown(errorBody.parseErrorMessage()))
        }

    } catch (e: UnknownHostException) {
        return Result.Error(Network.NoInternet(""))
    } catch (e: SocketTimeoutException) {
        return Result.Error(Network.RequestTimeOut(""))
    } catch (e: Exception) {
        e.printStackTrace()
        return Result.Error(Network.Unknown(""))
    }
}

// ====================================================================
//                        HELPER FUNCTIONS
// ====================================================================

fun String?.parseErrorMessage(): String {
    val defaultResponse: DefaultResponse? = try {
        Gson().fromJson(this, object : TypeToken<DefaultResponse>() {}.type)
    } catch (e: JsonSyntaxException) {
        null
    }
    return defaultResponse?.message ?: ""
}