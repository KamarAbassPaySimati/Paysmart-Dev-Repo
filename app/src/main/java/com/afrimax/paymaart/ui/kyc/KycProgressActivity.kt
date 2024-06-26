package com.afrimax.paymaart.ui.kyc

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.GetUserKycDataResponse
import com.afrimax.paymaart.data.model.KycUserData
import com.afrimax.paymaart.databinding.ActivityKycProgressBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.showLogE
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KycProgressActivity : BaseActivity() {
    private lateinit var b: ActivityKycProgressBinding
    private var kycScope = ""
    private var shouldReloadDocs = true
    private lateinit var infoResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var progressScope: String
    private var citizenship = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        b = ActivityKycProgressBinding.inflate(layoutInflater)

        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.kycProgressActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true

        progressScope = intent.getStringExtra(Constants.ONBOARD_PROGRESS_SCOPE)
            ?: Constants.ONBOARD_PROGRESS_SCOPE_INITIAL

        if (progressScope == Constants.ONBOARD_PROGRESS_SCOPE_INITIAL) showInitialProgressScreen()
        else showFinalProgressScreen()


        initViews()
        setUpListeners()
    }

    private fun initViews() {
        kycScope = intent.getStringExtra(Constants.KYC_SCOPE) ?: Constants.KYC_MALAWI_FULL

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (b.kycProgressActivityBackButton2Box.isVisible) {
                    //Navigate to Home screen if this is the only Activity in the stack
                    finishAffinity()
                } else {
                    val callbackIntent = Intent()
                    callbackIntent.putExtra(Constants.KYC_SCOPE, kycScope)
                    setResult(RESULT_CANCELED, callbackIntent)
                    finish()
                }
            }
        })

        infoResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                //Don't reload data from getUserKycDataApi
                if (result.resultCode == RESULT_OK || result.resultCode == RESULT_CANCELED) shouldReloadDocs =
                    false
            }
    }

    private fun setUpListeners() {

        b.kycProgressActivityBackButton1IV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.kycProgressActivityBackButton2IV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.kycProgressActivityKycGuideTV.setOnClickListener {
            val i = Intent(this, KycRegistrationGuideActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
            infoResultLauncher.launch(i, options)

        }

        b.kycProgressActivityCompleteKycButton.setOnClickListener {
            val i = Intent(this, KycSelectActivity::class.java)
            i.putExtra(Constants.KYC_CITIZENSHIP, citizenship)
            startActivity(i)
        }


        val ss = SpannableString(getString(R.string.kyc_process_text))
        val clickableSpanRegistrationProcess: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val i = Intent(
                    this@KycProgressActivity, KycRegistrationProcessActivity::class.java
                )
                val options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@KycProgressActivity)
                infoResultLauncher.launch(i, options)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpanRegistrationProcess, 11, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        b.kycProgressActivityKycProcessTV.movementMethod = LinkMovementMethod.getInstance()
        b.kycProgressActivityKycProcessTV.text = ss

        b.kycProgressActivityFinishButton.setOnClickListener {
            //Delete all temporary images created from the local storage
            deleteTempFiles()
            finishAffinity()
        }

        b.kycProgressActivityCompleteKycTV.setOnClickListener {
            val i = Intent(this, KycSelectActivity::class.java)
            startActivity(i)
            finishAffinity()
        }
    }

    private fun showInitialProgressScreen() {
        //Make back button visible
        b.kycProgressActivityBackButton2Box.visibility = View.VISIBLE
        b.kycProgressActivityBackButton1Box.visibility = View.GONE

        //Hide success text
        b.kycProgressActivitySuccessTV.visibility = View.GONE
        b.kycProgressActivityKycVerificationContainer.visibility = View.GONE

        //Make guide icon visible
        b.kycProgressActivityKycGuideTV.visibility = View.VISIBLE


        //Make button visible
        b.kycProgressActivityCompleteKycButtonContainer.visibility = View.VISIBLE
        b.kycProgressActivityFinishButtonContainer.visibility = View.GONE
    }

    private fun showFinalProgressScreen() {
        //Make back button visible
        b.kycProgressActivityBackButton2Box.visibility = View.GONE
        b.kycProgressActivityBackButton1Box.visibility = View.VISIBLE

        b.kycProgressActivityKycVerificationContainer.visibility = View.VISIBLE

        //Hide guide icon
        b.kycProgressActivityKycGuideTV.visibility = View.GONE

        //Make button visible
        b.kycProgressActivityCompleteKycButtonContainer.visibility = View.GONE
        b.kycProgressActivityFinishButtonContainer.visibility = View.VISIBLE
    }

    private fun unTickCheckBox() {
        b.kycProgressActivityKYCRegistrationDoneIV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_kyc_progress_not_done)
        b.kycProgressActivityKYCRegistrationDoneIV.setImageDrawable(
            ContextCompat.getDrawable(
                this, R.drawable.ico_done_black
            )
        )
    }

    private fun tickCheckBox() {
        b.kycProgressActivityKYCRegistrationDoneIV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_kyc_progress_done)
        b.kycProgressActivityKYCRegistrationDoneIV.setImageDrawable(
            ContextCompat.getDrawable(
                this, R.drawable.ico_done_white
            )
        )
    }

    private fun retrieveUserKycDataApi() {
        b.kycProgressActivityContentBox.visibility = View.GONE
        b.kycProgressActivityLoaderLottie.visibility = View.VISIBLE

        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val onboardUserKycDataCall = ApiClient.apiService.viewKyc(idToken)


            onboardUserKycDataCall.enqueue(object : Callback<GetUserKycDataResponse> {
                override fun onResponse(
                    call: Call<GetUserKycDataResponse>, response: Response<GetUserKycDataResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) populateProgress(body.data)
                    else showToast(getString(R.string.default_error_toast))
                }

                override fun onFailure(call: Call<GetUserKycDataResponse>, t: Throwable) {
                    //Failed to parse data - Which means the user has not yet started with KYC,  so show default screen
                    b.kycProgressActivityContentBox.visibility = View.VISIBLE
                    b.kycProgressActivityLoaderLottie.visibility = View.GONE

                }

            })
        }
    }

    private fun populateProgress(data: KycUserData) {
        citizenship = data.citizen ?: ""
        "Response".showLogE(data)
        val addressFilledStatus = data.address_details_status ?: ""
        val identityFilledStatus = data.id_details_status ?: ""
        val infoFilledStatus = data.info_details_status ?: ""

        if (addressFilledStatus.isNotEmpty() && identityFilledStatus.isNotEmpty() && infoFilledStatus.isNotEmpty()) {
            //by default we assume no screens are filled
            unTickCheckBox()

            //by default Hide success text
            b.kycProgressActivitySuccessTV.visibility = View.GONE

            val kycStatus = data.kyc_status
            val completedStatus = data.completed
            when {
                (kycStatus == null) -> {
                    //not started
                    b.kycProgressActivityKycVerificationSubTextTV.text =
                        getString(R.string.kyc_submit_text2)

                    b.kycProgressActivityKycRegistrationChipTV.text =
                        getString(R.string.not_started)
                    b.kycProgressActivityKycRegistrationChip.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_chip_not_started)
                    b.kycProgressActivityKycRegistrationChipTV.setTextColor(
                        ContextCompat.getColor(
                            this, R.color.neutralGreyPrimaryText
                        )
                    )
                }

                (kycStatus == Constants.KYC_STATUS_IN_PROGRESS && completedStatus == false) -> {
                    //in progress
                    b.kycProgressActivityKycVerificationSubTextTV.text =
                        getString(R.string.kyc_submit_text2)

                    b.kycProgressActivityKycRegistrationChipTV.text =
                        getString(R.string.in_progress)
                    b.kycProgressActivityKycRegistrationChip.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_chip_in_progress)
                    b.kycProgressActivityKycRegistrationChipTV.setTextColor(
                        ContextCompat.getColor(
                            this, R.color.accentInformation
                        )
                    )
                }

                kycStatus == Constants.KYC_STATUS_INFO_REQUIRED -> {
                    //further information required
                    b.kycProgressActivityKycVerificationSubTextTV.text =
                        getString(R.string.kyc_submit_text2)

                    b.kycProgressActivityKycRegistrationChipTV.text =
                        getString(R.string.further_information_required)
                    b.kycProgressActivityKycRegistrationChip.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_chip_rejected)
                    b.kycProgressActivityKycRegistrationChipTV.setTextColor(
                        ContextCompat.getColor(
                            this, R.color.accentNegative
                        )
                    )
                }

                kycStatus == Constants.KYC_STATUS_COMPLETED -> {
                    //completed
                    tickCheckBox()

                    b.kycProgressActivityKycVerificationSubTextTV.text =
                        getString(R.string.kyc_submit_text1)

                    b.kycProgressActivityKycRegistrationChipTV.text =
                        getString(R.string.completed)
                    b.kycProgressActivityKycRegistrationChip.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_chip_completed)
                    b.kycProgressActivityKycRegistrationChipTV.setTextColor(
                        ContextCompat.getColor(
                            this, R.color.accentPositive
                        )
                    )

                    //Hide complete kyc button
                    b.kycProgressActivityCompleteKycTV.visibility = View.GONE
                }

                (kycStatus == Constants.KYC_STATUS_IN_PROGRESS && completedStatus == true) -> {
                    //in review
                    tickCheckBox()

                    //Show success text if scope is ONBOARD_PROGRESS_SCOPE_FINAL
                    if (progressScope == Constants.ONBOARD_PROGRESS_SCOPE_FINAL) b.kycProgressActivitySuccessTV.visibility =
                        View.VISIBLE

                    b.kycProgressActivityKycVerificationSubTextTV.text =
                        getString(R.string.kyc_submit_text1)

                    b.kycProgressActivityKycRegistrationChipTV.text =
                        getString(R.string.in_review)
                    b.kycProgressActivityKycRegistrationChip.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_chip_in_review)
                    b.kycProgressActivityKycRegistrationChipTV.setTextColor(
                        ContextCompat.getColor(
                            this, R.color.accentWarning
                        )
                    )

                    //Hide complete kyc button
                    b.kycProgressActivityCompleteKycTV.visibility = View.GONE
                }

            }

            b.kycProgressActivityContentBox.visibility = View.VISIBLE
            b.kycProgressActivityLoaderLottie.visibility = View.GONE
        }
    }

    /**Delete all the files in the app directory*/
    private fun deleteTempFiles() {
        val directory = externalMediaDirs[0]
        val files = directory.listFiles()

        if (files != null) {
            for (file in files) {
                file.delete()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        //Fetch Kyc Details
        if (shouldReloadDocs) retrieveUserKycDataApi()
        else shouldReloadDocs = true
    }
}