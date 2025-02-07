package com.afrimax.paysimati.common.data.repository

enum class LoginModeType {
    PASSWORD_MODE, PIN_MODE
}
interface SharedPrefsRepository {
    var userPaySimatiId: String?
    var loginMode: LoginModeType?
    var fcmToken: String?
    fun clearPrefs()
}


