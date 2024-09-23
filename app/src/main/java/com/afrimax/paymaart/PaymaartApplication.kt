package com.afrimax.paymaart

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.util.AmplifyConfigCreator
import com.afrimax.paymaart.util.AuthCalls
import com.afrimax.paymaart.util.PrefsManager
import com.afrimax.paymaart.util.RecaptchaManager
import com.afrimax.paymaart.util.showLogE
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.hub.HubChannel
import com.amplifyframework.hub.HubEvent
import com.amplifyframework.hub.SubscriptionToken
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltAndroidApp
class PaymaartApplication: Application() {
    private var subscriptionToken: SubscriptionToken? = null
    private lateinit var authCalls: AuthCalls
    private lateinit var prefsManager: PrefsManager
    override fun onCreate() {
        super.onCreate()
        authCalls = AuthCalls()
        prefsManager = PrefsManager()
        val configJson = AmplifyConfigCreator().createJson()

        FirebaseApp.initializeApp(this@PaymaartApplication, FirebaseOptions.Builder().apply {
            setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
            setApplicationId(BuildConfig.FIREBASE_APP_ID)
            setApiKey(BuildConfig.FIREBASE_API_KEY)
            setStorageBucket(BuildConfig.FIREBASE_STORAGE_BUCKET)
        }.build())

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(AmplifyConfiguration.fromJson(configJson), applicationContext)
        } catch (e: AmplifyException) {
            //
        }
        RecaptchaManager.initialise(this)

        subscriptionToken = Amplify.Hub.subscribe(HubChannel.AUTH) { hubEvent: HubEvent<*> ->
            // Handle the event
            when (hubEvent.name) {
                AuthChannelEventName.SIGNED_IN.name -> setUpUser()
                AuthChannelEventName.SIGNED_OUT.name -> destroyUser()
                AuthChannelEventName.SESSION_EXPIRED.name -> destroyUser()
            }
        }

    }

    private suspend fun fetchFcmToken(): String {
        return suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                continuation.resume(it)
            }.addOnFailureListener {
                continuation.resume("")
            }
        }
    }

    private fun setUpUser() {
        with(ProcessLifecycleOwner.get()) {
            lifecycleScope.launch {
                val idToken = authCalls.fetchIdToken()
                if (idToken != null) {
                    //Fetch fcm token using Firebase sdk
                    val fcmToken = fetchFcmToken()

                    //Store the fcm token in backend
                    authCalls.storeFcmTokenApi(idToken, fcmToken)

                    //Store the fcm token locally
                    prefsManager.storeFcmToken(this@PaymaartApplication, fcmToken)
                }
            }
        }
    }

    private fun destroyUser() {
        //Clear local storage
        PrefsManager().clearPrefs(this@PaymaartApplication)

    }
}