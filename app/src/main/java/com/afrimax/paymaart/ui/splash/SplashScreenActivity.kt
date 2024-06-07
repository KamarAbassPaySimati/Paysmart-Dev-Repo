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
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.ActivitySplashScreenBinding
import com.afrimax.paymaart.ui.home.HomeActivity
import com.afrimax.paymaart.ui.intro.IntroActivity
import com.afrimax.paymaart.ui.kyc.KycProgressActivity
import com.amplifyframework.core.Amplify

private const val PROGRESS = "progress"
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var animator1: ObjectAnimator
    private lateinit var animator2: ObjectAnimator
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        amplifyFetchUserSession()
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
                if (isLoggedIn) startActivity(
                    Intent(
                        this@SplashScreenActivity, HomeActivity::class.java
                    )
                )
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

    private fun amplifyFetchUserSession() {
        binding.horizontalProgressBar.progress = 50
        animator1.cancel()
        animator2 = ObjectAnimator.ofInt(
            binding.horizontalProgressBar, PROGRESS, binding.horizontalProgressBar.progress, 100
        )
        Amplify.Auth.fetchAuthSession({
            runOnUiThread {
                startFinalAnim(it.isSignedIn)
            }
        }, { _ ->
            runOnUiThread {
                startFinalAnim(false)
            }
        })
    }

}