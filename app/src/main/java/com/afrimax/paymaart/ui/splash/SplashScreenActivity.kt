package com.afrimax.paymaart.ui.splash

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.afrimax.paymaart.MainActivity
import com.afrimax.paymaart.databinding.ActivitySplashScreenBinding
import com.afrimax.paymaart.ui.intro.IntroActivity
private const val PROGRESS = "progress"
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var animator1: ObjectAnimator
    private lateinit var animator2: ObjectAnimator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.horizontalProgressBar.progress = 0
        animator1 = ObjectAnimator.ofInt(
            binding.horizontalProgressBar, PROGRESS, binding.horizontalProgressBar.progress, 50
        )
        binding.horizontalProgressBar.progress = 50
        animator2 = ObjectAnimator.ofInt(
            binding.horizontalProgressBar, PROGRESS, binding.horizontalProgressBar.progress, 100
        )
        startInitialAnimator()
    }

    private fun startInitialAnimator(){
        animator1.apply {
            duration = 3000
            setAutoCancel(true)
            interpolator = DecelerateInterpolator()
            start()
        }
        animator1.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {}

            override fun onAnimationEnd(p0: Animator) {
                startFinalAnimator(false)
            }

            override fun onAnimationCancel(p0: Animator) {}

            override fun onAnimationRepeat(p0: Animator) {}

        })
    }

    private fun startFinalAnimator(isUserLoggedIn: Boolean){
        animator2.apply {
            duration = 500
            setAutoCancel(true)
            interpolator = DecelerateInterpolator()
            start()
        }
        animator2.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {}

            override fun onAnimationEnd(p0: Animator) {
                if (isUserLoggedIn) {
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                }else {
                    startActivity(Intent(this@SplashScreenActivity, IntroActivity::class.java))
                }
                finishAffinity()
            }

            override fun onAnimationCancel(p0: Animator) {}

            override fun onAnimationRepeat(p0: Animator) {}

        })
    }

}