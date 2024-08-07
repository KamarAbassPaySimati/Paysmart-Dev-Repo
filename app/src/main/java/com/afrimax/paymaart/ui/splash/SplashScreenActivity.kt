package com.afrimax.paymaart.ui.splash

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.ActivitySplashScreenBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.home.HomeActivity
import com.afrimax.paymaart.ui.intro.IntroActivity
import com.afrimax.paymaart.ui.membership.MembershipPlansActivity
import com.afrimax.paymaart.util.AuthCalls
import com.afrimax.paymaart.util.NotificationNavigation
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
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        action = intent.getStringExtra("action") ?: ""
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

    private fun startInitialAnimator(){
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
                }
                else startActivity(Intent(this@SplashScreenActivity, IntroActivity::class.java))
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
            animator2 = ObjectAnimator.ofInt(binding.horizontalProgressBar, "progress", binding.horizontalProgressBar.progress, 100)

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

    private fun handleNavigation(){
        val targetActivity: Class<out AppCompatActivity> = when (action) {
            NotificationNavigation.MEMBERSHIP_PLANS.screenName -> MembershipPlansActivity::class.java
            else -> HomeActivity::class.java
        }
        if (action.isNotEmpty() && targetActivity != HomeActivity::class.java) {
            val mainActivity = Intent(this@SplashScreenActivity, HomeActivity::class.java)
            val targetIntent = Intent(this@SplashScreenActivity, targetActivity)
            startActivities(arrayOf(mainActivity, targetIntent))
        }else {
            startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
        }
    }

}