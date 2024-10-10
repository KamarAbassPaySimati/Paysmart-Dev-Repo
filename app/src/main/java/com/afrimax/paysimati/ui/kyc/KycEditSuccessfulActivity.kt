package com.afrimax.paysimati.ui.kyc

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.SelfKycDetailsResponse
import com.afrimax.paysimati.data.model.ViewUserData
import com.afrimax.paysimati.databinding.ActivityKycEditSuccessfulBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.util.Constants
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KycEditSuccessfulActivity : BaseActivity() {
    private lateinit var b: ActivityKycEditSuccessfulBinding
    private lateinit var kycScope: String
    private lateinit var viewScope: String
    private lateinit var editScope: String
    private var sendEmail = true
    private lateinit var paymaartId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityKycEditSuccessfulBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(b.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true

        initViews()
        setUpLayout()
        setUpListeners()
    }

    private fun initViews() {
        kycScope = intent.getStringExtra(Constants.KYC_SCOPE) ?: ""
        viewScope = intent.getStringExtra(Constants.VIEW_SCOPE) ?: Constants.VIEW_SCOPE_FILL
        editScope = intent.getStringExtra(Constants.EDIT_SCOPE) ?: Constants.EDIT_SCOPE_SELF

        sendEmail = intent.getBooleanExtra(Constants.KYC_SEND_EMAIL, true)
        paymaartId = intent.getStringExtra(Constants.ONBOARD_USER_PAYMAART_ID) ?: ""

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val callbackIntent = Intent()
                callbackIntent.putExtra(Constants.KYC_SCOPE, kycScope)
                callbackIntent.putExtra(Constants.VIEW_SCOPE, viewScope)
                callbackIntent.putExtra(Constants.EDIT_SCOPE, editScope)
                callbackIntent.putExtra(Constants.ONBOARD_USER_PAYMAART_ID, paymaartId)
                callbackIntent.putExtra(Constants.KYC_SEND_EMAIL, sendEmail)
                setResult(RESULT_CANCELED, callbackIntent)
                finish()
            }
        })
    }

    private fun setUpLayout() {
        if (viewScope == Constants.VIEW_SCOPE_UPDATE) {
            b.kycEditSuccessfulActivityKycVerificationTV.text = getString(R.string.switch_to_full_kyc)
            b.kycEditSuccessfulActivityKycYourIdentityTV
                .setTextColor(ContextCompat.getColor(this, R.color.neutralGreyPrimaryText))
            b.kycEditSuccessfulActivityKycYourInfoTV
                .setTextColor(ContextCompat.getColor(this, R.color.neutralGreyPrimaryText))
        }
    }

    private fun setUpListeners() {
        b.kycEditSuccessfulActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.kycEditSuccessfulActivityFinishButton.setOnClickListener {
            finishAffinity()
        }

        b.kycEditSuccessfulActivityCompleteKycTV.setOnClickListener {
            val i = Intent(this, KycCustomerPersonalDetailsActivity::class.java)
            startActivity(i)
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        when (editScope) {
            Constants.EDIT_SCOPE_SELF -> getLatestSelfDataApi()
        }

    }

    private fun getLatestSelfDataApi() {
        b.kycEditSuccessfulActivityContentBox.visibility = View.GONE
        b.kycEditSuccessfulActivityLoaderLottie.visibility = View.VISIBLE

        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val getUserKycDataCall = ApiClient.apiService.getSelfKycUserData("Bearer $idToken")

            getUserKycDataCall.enqueue(object : Callback<SelfKycDetailsResponse> {
                override fun onResponse(
                    call: Call<SelfKycDetailsResponse>, response: Response<SelfKycDetailsResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) populateProgress(body.data)
                    else showToast(getString(R.string.default_error_toast))
                }

                override fun onFailure(call: Call<SelfKycDetailsResponse>, t: Throwable) {
                    showToast(getString(R.string.default_error_toast))
                }
            })
        }
    }

    private fun populateProgress(data: ViewUserData) {
        val addressFilledStatus = data.address_details_status ?: ""
        val identityFilledStatus = data.id_details_status ?: ""
        val infoFilledStatus = data.info_details_status ?: ""

        if (addressFilledStatus.isNotEmpty() && identityFilledStatus.isNotEmpty() && infoFilledStatus.isNotEmpty()) {

            //by default Hide success text
            b.kycEditSuccessfulActivitySuccessTV.visibility = View.GONE
            val kycStatus = data.kyc_status
            val completedStatus = data.completed
            when {
                (kycStatus == null) -> {
                    //not started
                    b.kycEditSuccessfulActivityKycVerificationSubTextTV.text =
                        getString(R.string.kyc_submit_text2)

                    b.kycEditSuccessfulActivityKycRegistrationChipTV.text =
                        getString(R.string.not_started)
                    b.kycEditSuccessfulActivityKycRegistrationChip.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_chip_not_started)
                    b.kycEditSuccessfulActivityKycRegistrationChipTV.setTextColor(
                        ContextCompat.getColor(
                            this, R.color.neutralGreyPrimaryText
                        )
                    )
                }

                (kycStatus == Constants.KYC_STATUS_IN_PROGRESS && completedStatus == false) -> {
                    //in progress
                    b.kycEditSuccessfulActivityKycVerificationSubTextTV.text =
                        getString(R.string.kyc_submit_text2)

                    b.kycEditSuccessfulActivityKycRegistrationChipTV.text =
                        getString(R.string.in_progress)
                    b.kycEditSuccessfulActivityKycRegistrationChip.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_chip_in_progress)
                    b.kycEditSuccessfulActivityKycRegistrationChipTV.setTextColor(
                        ContextCompat.getColor(
                            this, R.color.accentInformation
                        )
                    )
                }

                kycStatus == Constants.KYC_STATUS_INFO_REQUIRED -> {
                    //further information required
                    b.kycEditSuccessfulActivityKycVerificationSubTextTV.text =
                        getString(R.string.kyc_submit_text2)

                    b.kycEditSuccessfulActivityKycRegistrationChipTV.text =
                        getString(R.string.further_information_required)
                    b.kycEditSuccessfulActivityKycRegistrationChip.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_chip_rejected)
                    b.kycEditSuccessfulActivityKycRegistrationChipTV.setTextColor(
                        ContextCompat.getColor(
                            this, R.color.accentNegative
                        )
                    )
                }

                kycStatus == Constants.KYC_STATUS_COMPLETED -> {
                    //completed
                    b.kycEditSuccessfulActivityKycVerificationSubTextTV.text =
                        getString(R.string.kyc_submit_text1)

                    b.kycEditSuccessfulActivityKycRegistrationChipTV.text =
                        getString(R.string.completed)
                    b.kycEditSuccessfulActivityKycRegistrationChip.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_chip_completed)
                    b.kycEditSuccessfulActivityKycRegistrationChipTV.setTextColor(
                        ContextCompat.getColor(
                            this, R.color.accentPositive
                        )
                    )

                    //Hide complete kyc button
                    b.kycEditSuccessfulActivityCompleteKycTV.visibility = View.GONE
                }

                (kycStatus == Constants.KYC_STATUS_IN_PROGRESS && completedStatus == true) -> {
                    //in review
                    b.kycEditSuccessfulActivitySuccessTV.visibility = View.VISIBLE

                    b.kycEditSuccessfulActivityKycVerificationSubTextTV.text =
                        getString(R.string.kyc_submit_text1)

                    b.kycEditSuccessfulActivityKycRegistrationChipTV.text =
                        getString(R.string.in_review)
                    b.kycEditSuccessfulActivityKycRegistrationChip.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_chip_in_review)
                    b.kycEditSuccessfulActivityKycRegistrationChipTV.setTextColor(
                        ContextCompat.getColor(
                            this, R.color.accentWarning
                        )
                    )

                    //Hide complete kyc button
                    b.kycEditSuccessfulActivityCompleteKycTV.visibility = View.GONE
                }

            }

            b.kycEditSuccessfulActivityContentBox.visibility = View.VISIBLE
            b.kycEditSuccessfulActivityLoaderLottie.visibility = View.GONE
        }
    }
}