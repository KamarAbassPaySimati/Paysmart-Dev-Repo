package com.afrimax.paymaart.util

import android.content.Context
import android.content.SharedPreferences

class PrefsManager {

    fun storeLoginInfo(context: Context, paymaartId: String, loginMode: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(Constants.USER_DATA_PREFS, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Constants.PREF_KEY_PAYMAART_ID, paymaartId)
        editor.putString(Constants.PREF_KEY_LOGIN_MODE, loginMode)
        editor.apply()
        editor.commit()
    }

    fun storeFcmToken(context: Context, fcmToken: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(Constants.USER_DATA_PREFS, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Constants.PREF_KEY_FCM_TOKEN, fcmToken)
        editor.apply()
        editor.commit()
    }

    fun retrieveFcmToken(context: Context): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(Constants.USER_DATA_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Constants.PREF_KEY_FCM_TOKEN, null)
    }

    fun clearPrefs(context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(Constants.USER_DATA_PREFS, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}