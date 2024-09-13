package com.afrimax.paymaart.ui.kyc

import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.animation.AccelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.ActivityKycRegistrationProcessBinding

class KycRegistrationProcessActivity : AppCompatActivity() {
    private lateinit var b: ActivityKycRegistrationProcessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityKycRegistrationProcessBinding.inflate(layoutInflater)
        setAnimation()
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.kycRegistrationProcessActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        wic.isAppearanceLightNavigationBars = true

        b.kycRegistrationProcessActivityCloseIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setAnimation() {
        val slide = Slide()
        slide.slideEdge = Gravity.BOTTOM
        slide.setDuration(300)
        slide.setInterpolator(AccelerateInterpolator())

        window.enterTransition = slide
        window.returnTransition = slide
    }
}