package com.afrimax.paysimati.ui.reportMerchant

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.ReportMerchantRequest
import com.afrimax.paysimati.databinding.ActivityReportMerchantBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.utils.bottomsheets.ReportCompleteMessage
import com.afrimax.paysimati.ui.utils.bottomsheets.ReportMerchantOtherReasons
import com.afrimax.paysimati.ui.utils.interfaces.ReportOtherReason
import com.afrimax.paysimati.util.Constants
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.launch

class ReportMerchantActivity :BaseActivity(),ReportOtherReason {
    private lateinit var binding : ActivityReportMerchantBinding
    private var paymaartId =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportMerchantBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        paymaartId = intent.getStringExtra(Constants.PAYMAART_ID)?:""
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reportMerchantProfile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.viewMerchantActivityBackButton.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.checkOthers.setOnClickListener {
            ReportMerchantOtherReasons().show(supportFragmentManager,ReportMerchantOtherReasons.TAG)
        }
        binding.ReportMerchantsumbit.setOnClickListener {
            val selectedreason = mutableListOf<String>()

            if(binding.checkMerchantSecurity.isChecked){
                selectedreason.add(getString(R.string.report_merchant_q_1))
            }
            if(binding.checkMerchantDisputes.isChecked){
                selectedreason.add(getString(R.string.report_merchant_q_2))
            }

            if(binding.checkPrivacyConcerns.isChecked){
                selectedreason.add(getString(R.string.report_merchant_q_3))
            }
            if(binding.checkRegulatoryCompliance.isChecked){
                selectedreason.add(getString(R.string.report_merchant_q_4))
            }
            if(binding.checkOthers.isChecked){
                val otherReason = getOtherReason()
                if (!otherReason.isNullOrBlank()) {
                    selectedreason.add(otherReason)
                }
            }
            if (selectedreason.isEmpty()) {
                Toast.makeText(this, "Please select at least one reason", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            submitReport(selectedreason)

        }

    }

    private fun submitReport(reasons: List<String>) {
        showbuttonloader(
            binding.ReportMerchantsumbit,
            binding.payMerchantActivitySendPaymentButtonLoaderLottie
        )

        val request = ReportMerchantRequest(
            reasons = reasons,
            userId =  paymaartId
        )
        lifecycleScope.launch {
            val id =fetchIdToken()
            val response = ApiClient.apiService.reportMerchant(id,request)
            if (response.isSuccessful) {
                hideButtonLoader(
                    binding.ReportMerchantsumbit,
                    binding.payMerchantActivitySendPaymentButtonLoaderLottie,
                    getString(R.string.Report_Merchant)
                )
                ReportCompleteMessage().show(supportFragmentManager,ReportCompleteMessage.TAG)
            } else {
                showToast(getString(R.string.default_error_toast))
            }
        }
    }

    private var otherReason: String? = null

    override fun onReportReasonTyped(reason: String) {
        otherReason = reason
        if (reason.isEmpty()) {
            binding.checkOthers.isChecked = false
        }
    }

    private fun getOtherReason(): String? {
        return otherReason
    }

    private fun showbuttonloader(actionButton: AppCompatButton, loaderLottie: LottieAnimationView) {
        actionButton.text = getString(R.string.empty_string)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        loaderLottie.visibility = VISIBLE
    }

    private fun hideButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView, buttonText: String
    ) {
        actionButton.text = buttonText
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        loaderLottie.visibility = GONE
    }


}