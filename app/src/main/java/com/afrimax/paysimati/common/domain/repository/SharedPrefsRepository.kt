package com.afrimax.paysimati.common.data.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE

enum class LoginModeType {
    PASSWORD_MODE, PIN_MODE
}
interface SharedPrefsRepository {
    var userPaySimatiId: String?
    var loginMode: LoginModeType?
    var fcmToken: String?
    fun clearPrefs()
}

class SharedPrefsRepositoryImpl(private val context: Context) : SharedPrefsRepository {

    companion object {
        private const val SHARED_PREFS_NAME = "paysimati_prefs"
        private const val PAYSIMATI_ID = "paysimati_id"
        private const val LOGIN_MODE = "login_mode"
        private const val FCM_TOKEN = "fcm_token"
    }

    private val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)

    override var userPaySimatiId: String?
        get() = sharedPrefs.getString(PAYSIMATI_ID, null)?.uppercase()
        set(value) {
            sharedPrefs.edit().putString(PAYSIMATI_ID, value).apply()
        }

    override var loginMode: LoginModeType?
        get() = sharedPrefs.getString(LOGIN_MODE, null)?.let { LoginModeType.valueOf(it) }
        set(value) {
            value?.let {
                sharedPrefs.edit().putString(LOGIN_MODE, it.name).apply()
            }
        }

    override var fcmToken: String?
        get() = sharedPrefs.getString(FCM_TOKEN, null)
        set(value) {
            value?.let {
                sharedPrefs.edit().putString(FCM_TOKEN, it).apply()
            }
        }

    override fun clearPrefs() {
        sharedPrefs.edit().clear().apply()
    }
}
