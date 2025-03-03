package com.afrimax.paysimati.ui.chatMerchant.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.afrimax.paysimati.common.data.repository.LoginModeType
import com.afrimax.paysimati.common.data.repository.SharedPrefsRepository
import com.afrimax.paysimati.common.presentation.utils.*
import com.afrimax.paysimati.util.Constants.PREF_KEY_FCM_TOKEN
import com.afrimax.paysimati.util.Constants.PREF_KEY_LOGIN_MODE
import com.afrimax.paysimati.util.Constants.PREF_KEY_PAYMAART_ID
import com.afrimax.paysimati.util.Constants.USER_DATA_PREFS

class SharedPrefsRepositoryImpl(context: Context) : SharedPrefsRepository {

    private val sharedPrefs = context.getSharedPreferences(USER_DATA_PREFS, MODE_PRIVATE)

    override var userPaySimatiId: String?
        get() = sharedPrefs.getString(PREF_KEY_PAYMAART_ID, null)?.uppercase()
        set(value) {
            sharedPrefs.edit().putString(PREF_KEY_PAYMAART_ID, value).apply()
        }

    override var loginMode: LoginModeType?
        get() = sharedPrefs.getString(PREF_KEY_LOGIN_MODE, null)?.let { LoginModeType.valueOf(it) }
        set(value) {
            value?.let {
                sharedPrefs.edit().putString(PREF_KEY_LOGIN_MODE, it.name).apply()
            }
        }

    override var fcmToken: String?
        get() = sharedPrefs.getString(PREF_KEY_FCM_TOKEN, null)
        set(value) {
            value?.let {
                sharedPrefs.edit().putString(PREF_KEY_FCM_TOKEN, it).apply()
            }
        }

    override fun clearPrefs() {
        sharedPrefs.edit().clear().apply()
    }

}

