package com.afrimax.paymaart

import android.app.Application
import com.afrimax.paymaart.util.AmplifyConfigCreator
import com.afrimax.paymaart.util.RecaptchaManager
import com.afrimax.paymaart.util.showLogE
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.storage.s3.AWSS3StoragePlugin

class PaymaartApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        val configJson = AmplifyConfigCreator().createJson()

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(AmplifyConfiguration.fromJson(configJson), applicationContext)
        } catch (e: AmplifyException) {
            //
        }
        RecaptchaManager.initialise(this)
    }
}