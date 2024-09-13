package com.afrimax.paysimati.util

import android.app.Application
import com.afrimax.paysimati.BuildConfig
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object RecaptchaManager {

    private var recaptchaClient: RecaptchaClient? = null
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

     fun initialise(application: Application) {
        if (recaptchaClient == null ){
            scope.launch {
                Recaptcha.getClient(application, BuildConfig.SITE_KEY)
                    .onSuccess { client ->
                        recaptchaClient = client
                    }
                    .onFailure { exception ->
                        "Response".showLogE("Recaptcha Client Error: ${exception.message}")
                    }
            }
        }
    }

    fun getClient(): RecaptchaClient? = recaptchaClient
}