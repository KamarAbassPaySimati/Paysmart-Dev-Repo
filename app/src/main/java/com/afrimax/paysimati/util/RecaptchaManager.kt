package com.afrimax.paysimati.util

import android.app.Application
import com.afrimax.paysimati.BuildConfig
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaClient

class RecaptchaManager(val application: Application) {

    suspend fun getClient(): RecaptchaClient? {
        Recaptcha.getClient(application, BuildConfig.SITE_KEY).onSuccess { client ->
            return client
        }.onFailure { exception ->
            "Response".showLogE("Recaptcha Client Error: ${exception.message}")
        }
        return null
    }
}