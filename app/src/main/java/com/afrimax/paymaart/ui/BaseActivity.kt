package com.afrimax.paymaart.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Parcelable
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.afrimax.paymaart.util.Constants
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.core.Amplify
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

open class BaseActivity: AppCompatActivity() {
    fun retrievePaymaartId(): String {
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(Constants.USER_DATA_PREFS, Context.MODE_PRIVATE)
        val paymaartId = sharedPreferences.getString(Constants.PREF_KEY_PAYMAART_ID, null) ?: ""
        return paymaartId.uppercase()
    }

    fun retrieveLoginMode(): String? {
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(Constants.USER_DATA_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Constants.PREF_KEY_LOGIN_MODE, null)
    }

    fun setMembershipBannerVisibility(shown: Boolean){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(Constants.USER_DATA_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putBoolean(Constants.BANNER_VISIBILITY_FLAG, shown)
        }
    }

    fun getBannerVisibility() : Boolean {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(Constants.USER_DATA_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(Constants.BANNER_VISIBILITY_FLAG, true)
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

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyboard(view: View?, context: Context) {
        //Hide keyboard functionality for BottomSheets
        if (view != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showKeyboard(activity: Activity) {
        val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.showSoftInput(view, 0)
    }
}