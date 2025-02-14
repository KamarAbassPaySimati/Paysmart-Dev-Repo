package com.afrimax.paysimati.util

import android.Manifest
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.core.log
import com.afrimax.paysimati.common.presentation.utils.VIEW_MODEL_STATE
import com.afrimax.paysimati.ui.chatMerchant.data.chat.ChatState
import com.afrimax.paysimati.ui.chatMerchant.ui.ChatMerchantActivity
import com.afrimax.paysimati.ui.home.HomeActivity
import com.afrimax.paysimati.ui.membership.MembershipPlansActivity
import com.afrimax.paysimati.ui.paymerchant.ListMerchantTransactionActivity
import com.afrimax.paysimati.ui.splash.SplashScreenActivity
import com.afrimax.paysimati.ui.viewtransactions.ViewSpecificTransactionActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import kotlinx.coroutines.launch


class MessagingService(
    private val authCalls: AuthCalls = AuthCalls(),
    private val prefsManager: PrefsManager = PrefsManager()
) : FirebaseMessagingService() {

    /**The handleIntent() method is invoked for both foreground and background push notifications.
     * It converts the incoming intent into a NotificationData model to extract the notification data,
     * and then calls a function to display the notification.*/
    override fun handleIntent(data: Intent?) {

        if (data != null) {
            data.log()
            val isSilent = data.getStringExtra(IS_SILENT).toBoolean()
            if (isSilent) handleSilentPush(data)
            else showNotification(data)
        }
    }

    /**This function is triggered when the user's FCM token changes.
     * It calls two APIs: the first to delete the current FCM token from the server and the second to store the new token on the server.*/
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        with(ProcessLifecycleOwner.get()) {
            lifecycleScope.launch {
                val idToken = authCalls.fetchIdToken()

                //Fetch recent fcm token from local storage
                val recentFcmToken = prefsManager.retrieveFcmToken(this@MessagingService)

                //Call API to delete recent fcm token from backend
                if (idToken != null && recentFcmToken != null) authCalls.deleteFcmTokenApi(
                    idToken, recentFcmToken
                )

                //Store the new token in the backend
                if (idToken != null) authCalls.storeFcmTokenApi(idToken, token)

                //Store latest fcm token in local storage
                prefsManager.storeFcmToken(this@MessagingService, token)
            }
        }
    }

    private fun handleSilentPush(data: Intent) {
        val action = data.getStringExtra(ACTION)
        with(ProcessLifecycleOwner.get()) {
            lifecycleScope.launch {
                when (action) {
                    ACTION_LOGOUT -> {
                        authCalls.initiateLogout(this@MessagingService)
                        val i = applicationContext
                            .packageManager
                            .getLaunchIntentForPackage(applicationContext.packageName)
                        applicationContext.startActivity(
                            Intent
                                .makeRestartActivityTask(i!!.component)
                                .apply { setPackage(applicationContext.packageName) }
                        )
                    }
                }
            }
        }
    }

    /**The showNotification() function creates the notification channel the first time the app is opened.
     * Subsequently, the channel is not recreated. Each time the function is called,
     * it builds and displays the notification.*/
    private fun showNotification(data: Intent) {
        //Create the channel first | This only happens once when the app is first booting
        "Response".showLogE(Gson().toJson(data))
        createNotificationChannel()
        var userinfo :String?=null
        var requestuserinfo :String?=null
        var transactionId: String? = null
        val action =  data.getStringExtra(ACTION).toString()
        if (action == NotificationNavigation.TRANSACTIONS.screenName) transactionId =
            data.getStringExtra(TXN_ID).toString()
        if(action == NotificationNavigation.CHAT.screenName || action==NotificationNavigation.PAYREQUEST.screenName) userinfo =
            data.getStringExtra("user_info")
        Log.d("kk","$userinfo")
        val targetActivity: Class<out AppCompatActivity> = when (action) {
            NotificationNavigation.MEMBERSHIP_PLANS.screenName -> MembershipPlansActivity::class.java
            NotificationNavigation.TRANSACTIONS.screenName -> ViewSpecificTransactionActivity::class.java
            NotificationNavigation.PAYREQUEST.screenName -> ChatMerchantActivity::class.java
            NotificationNavigation.CHAT.screenName -> ChatMerchantActivity ::class.java


            else -> SplashScreenActivity::class.java
        }

        val receiver = Gson().fromJson(userinfo, ChatState::class.java)
        val intent = if (isAppInForeground()) {
            Intent(this, targetActivity).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                if (targetActivity == ViewSpecificTransactionActivity::class.java)
                    putExtra(Constants.TRANSACTION_ID, transactionId)
                if (targetActivity == ChatMerchantActivity::class.java)
                    if(action == NotificationNavigation.PAYREQUEST.screenName){
                        val receiver = Gson().fromJson(requestuserinfo, ChatState::class.java)
                        putExtra(VIEW_MODEL_STATE, ChatState(
                            receiverName = receiver.receiverName ,
                            receiverId = receiver.receiverId,
                            receiverProfilePicture = receiver.receiverProfilePicture,
                        )
                        )

                    }
                else{
                        putExtra(VIEW_MODEL_STATE, ChatState(
                            receiverName = receiver.receiverName ,
                            receiverId = receiver.receiverId,
                            receiverProfilePicture = receiver.receiverProfilePicture,
                        )
                        )
                    }

            }
        }else{
            Intent(this, SplashScreenActivity::class.java).apply {
                putExtra(Constants.ACTION, action)
                putExtra(Constants.TRANSACTION_ID, transactionId)
                putExtra(Constants.USER_INFO, userinfo)
            }
        }
        val notificationId = getUniqueNotificationId()

        // Create a PendingIntent to cancel the notification when clicked
        val cancelIntent = PendingIntent.getBroadcast(
            this,
            notificationId,
            Intent(this, NotificationDismissReceiver::class.java).apply {
                putExtra("notificationId", notificationId)
            },
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val title = data.getStringExtra(TITLE)
        val body = data.getStringExtra(BODY)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ico_notification)
            setContentTitle(title)
            setContentText(body)
            setAutoCancel(true)
            setSound(defaultSoundUri)
            setContentIntent(pendingIntent)
            setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            setGroup(GROUP_KEY)
        }


        if (ActivityCompat.checkSelfPermission(
                this@MessagingService, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) NotificationManagerCompat.from(this).apply {
            notify(getUniqueNotificationId(), notificationBuilder.build())
            notify(GROUP_KEY.hashCode(), buildGroupSummaryNotification())
        }
    }

    /**A notification channel is required in the latest versions of Android.
     * This function creates the notification channel.*/
    private fun createNotificationChannel() {
        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_PAYMENT, NotificationManager.IMPORTANCE_HIGH
            ).apply { setShowBadge(true) }
            notificationManager.createNotificationChannel(channel)
        }
    }


    /**The buildGroupSummaryNotification() function prompts the Android system to check
     * for notifications from the same app and group them together.
     * This is necessary to prevent each notification from appearing individually.*/
    private fun buildGroupSummaryNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setGroup(GROUP_KEY) // Use the same group key for the summary notification
            setGroupSummary(true)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setSmallIcon(R.drawable.ico_notification)
        }.build()
    }


    private fun getUniqueNotificationId(): Int {
        // Implement your logic to generate a unique ID for each notification
        return System.currentTimeMillis().toInt() + 111
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        if (appProcesses != null) {
            val packageName = packageName
            for (appProcess in appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcess.processName == packageName) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        const val CHANNEL_PAYMENT = "Payment Notifications"
        const val GROUP_KEY = "${BuildConfig.APPLICATION_ID}_group"
        const val CHANNEL_ID = "${BuildConfig.APPLICATION_ID}_channel_id"

        //payload keys
        const val IS_SILENT = "is_silent"
        const val TITLE = "title"
        const val BODY = "body"
        const val ACTION = "action"
        const val TXN_ID = "txn_id"

        //payload_values
        const val ACTION_LOGOUT = "logout"
    }
}
class NotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notificationId", 0)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(notificationId) // This cancels the notification when clicked
    }
}



enum class NotificationNavigation(val screenName: String){
    MEMBERSHIP_PLANS("membership"),
    TRANSACTIONS("transactions"),
    PAYREQUEST("request"),
    CHAT("new_chat")
}
