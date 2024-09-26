package com.afrimax.paysimati.ui.splash

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.databinding.ActivitySplashScreenBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.home.HomeActivity
import com.afrimax.paysimati.ui.intro.IntroActivity
import com.afrimax.paysimati.ui.membership.MembershipPlansActivity
import com.afrimax.paysimati.ui.viewtransactions.ViewSpecificTransactionActivity
import com.afrimax.paysimati.util.AuthCalls
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.NotificationNavigation
import com.amplifyframework.core.Amplify
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val PROGRESS = "progress"

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var animator1: ObjectAnimator
    private lateinit var animator2: ObjectAnimator
    private lateinit var action: String
    private lateinit var authCalls: AuthCalls
    private var transactionId: String = ""

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        action = intent.getStringExtra("action") ?: ""
        transactionId = intent.getStringExtra(Constants.TRANSACTION_ID) ?: ""
        authCalls = AuthCalls()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splashScreenActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false
        window.navigationBarColor = ContextCompat.getColor(this, R.color.primaryColor)
        binding.horizontalProgressBar.progress = 0
        animator1 = ObjectAnimator.ofInt(
            binding.horizontalProgressBar, PROGRESS, binding.horizontalProgressBar.progress, 50
        )
        startInitialAnimator()
        validateUserAuth()
    }

    private fun startInitialAnimator() {
        animator1.apply {
            duration = 3000
            setAutoCancel(true)
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    private fun startFinalAnim(isLoggedIn: Boolean) {
        animator2.setDuration(500)
        animator2.setAutoCancel(true)
        animator2.interpolator = DecelerateInterpolator()
        animator2.start()

        animator2.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
                //
            }

            override fun onAnimationEnd(p0: Animator) {
                if (isLoggedIn) {
                    handleNavigation()
                } else startActivity(Intent(this@SplashScreenActivity, IntroActivity::class.java))
                finish()
            }

            override fun onAnimationCancel(p0: Animator) {
                //
            }

            override fun onAnimationRepeat(p0: Animator) {
                //
            }

        })
    }

    private fun validateUserAuth() {
        lifecycleScope.launch {
            val amplifyLoginStatus = amplifyFetchUserSession()
            val paymaartId = retrievePaymaartId()

            animator1.cancel()
            animator2 = ObjectAnimator.ofInt(
                binding.horizontalProgressBar,
                "progress",
                binding.horizontalProgressBar.progress,
                100
            )

            if (amplifyLoginStatus && paymaartId.isNotEmpty()) {
                startFinalAnim(true)
            } else if (!amplifyLoginStatus && paymaartId.isNotEmpty()) {
                authCalls.initiateLogout(this@SplashScreenActivity)
                startFinalAnim(false)
            } else {
                startFinalAnim(false)
            }
        }
    }

    private suspend fun amplifyFetchUserSession(): Boolean {
        return suspendCoroutine { continuation ->
            Amplify.Auth.fetchAuthSession({
                continuation.resume(it.isSignedIn)
            }, {
                continuation.resume(false)
            })
        }
    }

    private fun handleNavigation() {
        val targetIntent = when (action) {
            NotificationNavigation.MEMBERSHIP_PLANS.screenName -> Intent(
                this@SplashScreenActivity, MembershipPlansActivity::class.java
            )

            NotificationNavigation.TRANSACTIONS.screenName -> {
                Intent(
                    this@SplashScreenActivity, ViewSpecificTransactionActivity::class.java
                ).apply {
                    putExtra(Constants.TRANSACTION_ID, transactionId)
                }
            }

            else -> Intent(this@SplashScreenActivity, HomeActivity::class.java)
        }
        if (action.isNotEmpty() && targetIntent.component?.className != HomeActivity::class.java.name) {
            val mainActivity = Intent(this@SplashScreenActivity, HomeActivity::class.java)
            startActivities(arrayOf(mainActivity, targetIntent))
        } else {
            startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
        }
    }

}