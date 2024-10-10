package com.afrimax.paysimati.ui.kyc

import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.afrimax.paysimati.R
import com.afrimax.paysimati.databinding.ActivityKycRegistrationGuideBinding

class KycRegistrationGuideActivity : AppCompatActivity() {
    private lateinit var b: ActivityKycRegistrationGuideBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityKycRegistrationGuideBinding.inflate(layoutInflater)
        setAnimation()
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.kycRegistrationGuideActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        wic.isAppearanceLightNavigationBars = true

        initViews()
        setUpListeners()
    }


    private fun initViews() {
        //
    }

    private fun setUpListeners() {
        b.kycRegistrationGuideActivityCloseIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.kycRegistrationGuideActivityMalawiSimplifiedKycContainer.setOnClickListener {
            toggleMalawiSimplifiedDetails()
        }

        b.kycRegistrationGuideActivityMalawiFullKycContainer.setOnClickListener {
            toggleMalawiFullDetails()
        }

        b.kycRegistrationGuideActivityNonMalawiKycContainer.setOnClickListener {
            toggleNonMalawiDetails()
        }
    }

    private fun toggleMalawiSimplifiedDetails() {
        if (b.kycRegistrationGuideActivityMalawiSimplifiedKycTextContainer.root.isVisible) {
            b.kycRegistrationGuideActivityMalawiSimplifiedKycNextArrowIV.animate().rotation(0f)
                .setDuration(200)
            b.kycRegistrationGuideActivityMalawiSimplifiedKycTextContainer.root.visibility =
                View.GONE
        } else {
            b.kycRegistrationGuideActivityMalawiSimplifiedKycNextArrowIV.animate().rotation(90f)
                .setDuration(200)
            b.kycRegistrationGuideActivityMalawiSimplifiedKycTextContainer.root.visibility =
                View.VISIBLE
        }
    }

    private fun toggleMalawiFullDetails() {
        if (b.kycRegistrationGuideActivityMalawiFullKycTextContainer.root.isVisible) {
            b.kycRegistrationGuideActivityMalawiFullKycNextArrowIV.animate().rotation(0f)
                .setDuration(200)
            b.kycRegistrationGuideActivityMalawiFullKycTextContainer.root.visibility = View.GONE
        } else {
            b.kycRegistrationGuideActivityMalawiFullKycNextArrowIV.animate().rotation(90f)
                .setDuration(200)
            b.kycRegistrationGuideActivityMalawiFullKycTextContainer.root.visibility = View.VISIBLE
        }
    }

    private fun toggleNonMalawiDetails() {
        if (b.kycRegistrationGuideActivityNonMalawiKycTextContainer.root.isVisible) {
            b.kycRegistrationGuideActivityNonMalawiKycNextArrowIV.animate().rotation(0f)
                .setDuration(200)
            b.kycRegistrationGuideActivityNonMalawiKycTextContainer.root.visibility = View.GONE
        } else {
            b.kycRegistrationGuideActivityNonMalawiKycNextArrowIV.animate().rotation(90f)
                .setDuration(200)
            b.kycRegistrationGuideActivityNonMalawiKycTextContainer.root.visibility = View.VISIBLE
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