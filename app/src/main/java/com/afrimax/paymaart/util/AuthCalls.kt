package com.afrimax.paymaart.util

import android.content.Context
import com.afrimax.paymaart.data.model.DefaultResponse
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult
import com.amplifyframework.core.Amplify
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthCalls {

    /**A coroutine function is used to retrieve the user ID token for secure API calls.*/
    suspend fun fetchIdToken(): String? {
        return suspendCoroutine { continuation ->
            Amplify.Auth.fetchAuthSession({
                it as AWSCognitoAuthSession
                continuation.resume(it.userPoolTokensResult.value?.idToken)
            }, {
                continuation.resume(null)
            })
        }
    }

    /**This function is used to call the API to store the user's FCM token.
     * It is invoked every time a user logs in and in the overridden onNewToken() method.*/
    suspend fun storeFcmTokenApi(idToken: String, fcmToken: String): Boolean {
        return suspendCoroutine { continuation ->
//            val storeFcmTokenCall = ApiClient.apiService.storeFcmToken(
//                idToken, FcmTokenRequest(
//                    notificationId = fcmToken
//                )
//            )
//            storeFcmTokenCall.enqueue(object : Callback<DefaultResponse> {
//                override fun onResponse(
//                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
//                ) {
//                    continuation.resume(response.isSuccessful)
//                }
//
//                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
//                    continuation.resume(false)
//                }
//            })
        }
    }

    /**This function deletes any previous FCM tokens from the backend database to prevent unnecessary storage consumption.*/
    suspend fun deleteFcmTokenApi(idToken: String, fcmToken: String): Boolean {
        return suspendCoroutine { continuation ->
//            val storeFcmTokenCall = ApiClient.apiService.deleteFcmToken(
//                idToken, FcmTokenRequest(
//                    notificationId = fcmToken
//                )
//            )
//            storeFcmTokenCall.enqueue(object : Callback<DefaultResponse> {
//                override fun onResponse(
//                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
//                ) {
//                    continuation.resume(response.isSuccessful)
//                }
//
//                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
//                    continuation.resume(false)
//                }
//            })
        }
    }

    /**This function performs the initial steps before logging out.
     * Before logging out, the user should delete their current FCM token from the database to avoid unnecessary storage usage.
     * The current FCM token is fetched from local storage. Regardless of whether the delete API call is successful,
     * the user is logged out using Amplify as the final step.*/
    suspend fun initiateLogout(context: Context) {
        val idToken = fetchIdToken()

        //Retrieve current fcm token from local storage
        val currentFcmToken = PrefsManager().retrieveFcmToken(context)
        if (idToken != null && currentFcmToken != null) {
            //Call API to delete this token from server
            deleteFcmTokenApi(idToken, currentFcmToken)
        }

        //Logout the user from front-end through Amplify
        makeAmplifyLogout()

        return suspendCoroutine { continuation ->
            continuation.resume(Unit)
        }

    }

    /**Native Amplify SDK logout method to log the user out of the app.*/
    private suspend fun makeAmplifyLogout(): Boolean {
        return suspendCoroutine { continuation ->
            Amplify.Auth.signOut { signOutResult ->
                when (signOutResult) {
                    is AWSCognitoAuthSignOutResult.CompleteSignOut -> continuation.resume(true)
                    else -> continuation.resume(false)
                }
            }
        }
    }
}