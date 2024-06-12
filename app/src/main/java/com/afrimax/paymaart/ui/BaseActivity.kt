package com.afrimax.paymaart.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Parcelable
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afrimax.paymaart.util.Constants
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.core.Amplify
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

open class BaseActivity: AppCompatActivity() {
    fun retrievePaymaartId(): String? {
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(Constants.USER_DATA_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Constants.PREF_KEY_PAYMAART_ID, null)
    }

    fun retrieveLoginMode(): String? {
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(Constants.USER_DATA_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Constants.PREF_KEY_LOGIN_MODE, null)
    }

    fun Context.toPx(dp: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
    ).toInt()

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    /**This function returns the Bearer token used for all the private APIs*/
    suspend fun fetchIdToken(): String {
        return suspendCoroutine { continuation ->
            Amplify.Auth.fetchAuthSession({
                it as AWSCognitoAuthSession
                val result = it.userPoolTokensResult.value
                if (result != null) continuation.resume(result.idToken.toString())
            }, {
                continuation.resume("")
            })
        }
    }
}