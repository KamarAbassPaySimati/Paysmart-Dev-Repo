package com.afrimax.paymaart.ui.membership

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.ApiService
import com.afrimax.paymaart.data.model.SubscriptionDetails
import com.afrimax.paymaart.data.model.SubscriptionDetailsRequestBody
import com.afrimax.paymaart.data.model.SubscriptionDetailsResponse
import com.afrimax.paymaart.data.model.UpdateAutoRenewalRequestBody
import com.afrimax.paymaart.databinding.ActivityPurchasedMembershipPlanViewBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.home.MembershipType
import com.afrimax.paymaart.ui.payment.PaymentSuccessfulActivity
import com.afrimax.paymaart.ui.utils.bottomsheets.MembershipPlansPurchaseBottomSheet
import com.afrimax.paymaart.ui.utils.bottomsheets.MembershipPlansPurchaseBottomSheetOnRenewal
import com.afrimax.paymaart.ui.utils.bottomsheets.TotalAmountReceiptBottomSheet
import com.afrimax.paymaart.ui.utils.interfaces.SendPaymentInterface
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.getDrawableExt
import com.afrimax.paymaart.util.showLogE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PurchasedMembershipPlanViewActivity : BaseActivity(), SendPaymentInterface {
    private lateinit var binding: ActivityPurchasedMembershipPlanViewBinding
    private var membershipPlan: MembershipPlanModel? = null
    private lateinit var renewalType: String
    private var membershipPlanTypes: ArrayList<RenewalPlans>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchasedMembershipPlanViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.purchasedMembershipPlanView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.offWhite)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.offWhite)
        initViews()
        getSubscriptionDetailsApi()
    }

    private fun initViews() {
        membershipPlan = intent.parcelable<MembershipPlanModel>(Constants.MEMBERSHIP_MODEL)
        renewalType = intent.getStringExtra(Constants.RENEWAL_TYPE) ?: ""
        membershipPlanTypes = intent.getParcelableArrayListExtra(Constants.MEMBERSHIP_PLANS)
    }

    private val getReferenceNumber: String
        get() = membershipPlan?.referenceNumber ?: ""

    private val getSubType: String
        get() = membershipPlan?.membershipType ?: ""

    private val getAutoRenew: Boolean
        get() = membershipPlan?.renewalType == true

    private fun setUpView(subscriptionDetails: SubscriptionDetails){
        val membershipType = membershipPlan?.membershipType
        val autoRenewal = if (membershipPlan?.renewalType == true) { "On" } else "Off"
        val validity = membershipPlan?.validity
        val paymentType = membershipPlan?.paymentType
        val startDate = getDate(subscriptionDetails.membershipStart)
        val endDate = getDate(subscriptionDetails.membershipExpiry)
        val membershipTypeNew = if (membershipPlan?.membershipType == MembershipType.PRIME.type) MembershipType.PRIME else MembershipType.PRIMEX
        when (membershipType) {
            MembershipType.PRIME.type -> {
                val primeColor = getColor(R.color.primeMembershipGrey)
                binding.purchasedMembershipPlanMembershipType.apply {
                    text = getString(R.string.membership_type_formatted, MembershipType.PRIME.displayName)
                    setTextColor(primeColor)
                }
                binding.purchasedMembershipPlanOptions.apply{
                    text = getString(R.string.membership_plan_formatted, validity, paymentType, autoRenewal)
                    setTextColor(primeColor)
                }
                binding.purchasedMembershipPlanValidity.apply{
                    text = getString(R.string.membership_plan_validity_formatted, startDate, endDate)
                    setTextColor(primeColor)
                }
                binding.purchasedMembershipPlanImageBg.background = this.getDrawableExt(R.drawable.ic_prime_membership_card_bg)
            }
            MembershipType.PRIMEX.type -> {
                val primeXColor = getColor(R.color.primeXCardBackground)
                binding.purchasedMembershipPlanMembershipType.apply {
                    text = getString(R.string.membership_type_formatted, MembershipType.PRIMEX.displayName)
                    setTextColor(primeXColor)
                }
                binding.purchasedMembershipPlanOptions.apply{
                    text = getString(R.string.membership_plan_formatted, validity, paymentType, autoRenewal)
                    setTextColor(primeXColor)
                }
                binding.purchasedMembershipPlanValidity.apply{
                    text = getString(R.string.membership_plan_validity_formatted, startDate, endDate)
                    setTextColor(primeXColor)
                }
                binding.purchasedMembershipPlanImageBg.background = this.getDrawableExt(R.drawable.ic_prime_x_membership_card_bg)
            }
            MembershipType.GO.type-> {
                //This scenario won't happen
                val goColor = getColor(R.color.goMemberStrokeColor)
                binding.purchasedMembershipPlanMembershipType.apply {
                    text = getString(R.string.membership_type_formatted, MembershipType.GO.displayName)
                    setTextColor(goColor)
                }
                binding.purchasedMembershipPlanOptions.apply{
                    text = getString(R.string.membership_plan_formatted, validity, paymentType, autoRenewal)
                    setTextColor(goColor)
                }
                binding.purchasedMembershipPlanValidity.apply{
                    text = getString(R.string.membership_plan_validity_formatted, startDate, endDate)
                    setTextColor(goColor)
                }
                binding.purchasedMembershipPlanImageBg.background = this.getDrawableExt(R.drawable.ic_go_membership_card_bg)
            }
        }

        if (renewalType == Constants.NEW_SUBSCRIPTION) {
            binding.purchasedMembershipPlansSubmitButtonContainer.visibility = View.VISIBLE
            binding.purchasedMembershipPlanRenewContainer.visibility = View.GONE
        }else{
            binding.purchasedMembershipPlansSubmitButtonContainer.visibility = View.GONE
            binding.purchasedMembershipPlanRenewContainer.visibility = View.VISIBLE
        }

        binding.purchasedMembershipPlansSubmitButton.setOnClickListener {
            //If user clicks on send payment after a failure then hide the error message
            binding.purchasedMembershipPlanErrorTV.visibility = View.GONE
            onSendPaymentClicked(subscriptionDetails)
        }

        binding.purchasedMembershipPlanRenewOnExpButton.setOnClickListener {
            if (membershipPlanTypes != null) {
                val membershipPlansList = membershipPlanTypes?.toMutableList() ?: emptyList()
                val membershipPurchasePlansSheet = MembershipPlansPurchaseBottomSheetOnRenewal(membershipTypeNew, membershipPlansList)
                membershipPurchasePlansSheet.isCancelable = false
                membershipPurchasePlansSheet.show(supportFragmentManager, MembershipPlansPurchaseBottomSheet.TAG)
            }
        }

        binding.purchasedMembershipPlanViewContentBox.visibility = View.VISIBLE
        binding.purchasedMembershipPlanLoaderLottie.visibility = View.GONE

        binding.purchasedMembershipActivateRenewNowButton.setOnClickListener {
            onActivateAutoRenew()
        }

        binding.addBankActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getSubscriptionDetailsApi() {
        binding.purchasedMembershipPlanViewContentBox.visibility = View.GONE
        binding.purchasedMembershipPlanLoaderLottie.visibility = View.VISIBLE
        lifecycleScope.launch {
            val idToken = fetchIdToken()

            val subscriptionDetails = ApiClient.apiService.getSubscriptionDetails(
                idToken, SubscriptionDetailsRequestBody(
                    referenceNumber = getReferenceNumber,
                    subType = getSubType.lowercase(),
                    autoRenew = getAutoRenew
                )
            )

            subscriptionDetails.enqueue(object : Callback<SubscriptionDetailsResponse> {
                override fun onResponse(
                    call: Call<SubscriptionDetailsResponse>,
                    response: Response<SubscriptionDetailsResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) setUpView(body.subscriptionDetails)
                    else showToast(getString(R.string.default_error_toast))

                }

                override fun onFailure(call: Call<SubscriptionDetailsResponse>, t: Throwable) {
                    showToast(getString(R.string.default_error_toast))
                }

            })
        }
    }


    private fun onSendPaymentClicked(subscriptionDetails: SubscriptionDetails) {
        val subscriptionDetailsRequestBody = SubscriptionDetailsRequestBody(
            referenceNumber = getReferenceNumber,
            subType = getSubType.lowercase(),
            autoRenew = getAutoRenew
        )
        val totalAmountReceiptBottomSheet =TotalAmountReceiptBottomSheet(subscriptionDetails, subscriptionDetailsRequestBody)
        totalAmountReceiptBottomSheet.show(supportFragmentManager, TotalAmountReceiptBottomSheet.TAG)
    }

    private fun onActivateAutoRenew() {
        showLoader()
        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val autoRenewalHandler = ApiClient.apiService.updateAutoRenewal(idToken, UpdateAutoRenewalRequestBody(autoRenew = true))

            autoRenewalHandler.enqueue(object : Callback<Unit>{
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful){
                        finish()
                    }else{
                        showToast(getString(R.string.default_error_toast))
                    }
                    hideLoader()
                }

                override fun onFailure(call: Call<Unit>, throwable: Throwable) {
                    hideLoader()
                    showToast(getString(R.string.default_error_toast))
                }
            })

        }
    }

    private fun showButtonLoader() {
        binding.purchasedMembershipPlansSubmitButton.apply {
            text = getString(R.string.empty_string)
            isEnabled = false
        }
        binding.purchasedMembershipPlansSubmitButtonLoaderLottie.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideButtonLoader() {
        binding.purchasedMembershipPlansSubmitButton.apply {
            text = getString(R.string.send_payment)
            isEnabled = true
        }
        binding.purchasedMembershipPlansSubmitButtonLoaderLottie.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun showLoader() {
        binding.purchasedMembershipActivateRenewNowButtonLottieLoader.visibility = View.VISIBLE
        binding.purchasedMembershipActivateRenewNowButton.apply {
            text = getString(R.string.empty_string)
            isEnabled = false
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideLoader() {
        binding.purchasedMembershipActivateRenewNowButtonLottieLoader.visibility = View.GONE
        binding.purchasedMembershipActivateRenewNowButton.apply {
            text = getString(R.string.activate_auto_renew_now)
            isEnabled = true
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    /**
        private fun getStartDate(): String {
            val currentDate = Date()
            val dateFormat = dateFormat
            return dateFormat.format(currentDate)
        }

        private fun getEndDate(startDate: String, validity: String?): String{
            if (validity.isNullOrEmpty())
                return ""
            val dateFormat = dateFormat
            val calendar = Calendar.getInstance()
            calendar.time = dateFormat.parse(startDate)!!
            calendar.add(Calendar.DAY_OF_YEAR, validity.toInt())
            val futureDate = calendar.time
            return dateFormat.format(futureDate)
        }
     */

    private fun getDate(timeStamp: Long): String{
        val newTimeStamp = timeStamp * 1000
        val date = Date(newTimeStamp)
        val dateFormat = dateFormat
        return dateFormat.format(date)
    }

    private val dateFormat: SimpleDateFormat
        get() = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    override fun onPaymentSuccess(successData: Any?) {
        val intent = Intent(this, PaymentSuccessfulActivity::class.java)
        val sceneTransitions = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
        if (successData != null) {
            intent.putExtra(Constants.SUCCESS_PAYMENT_DATA, successData as Parcelable)
        }
        startActivity(intent, sceneTransitions)
        finishAfterTransition()
    }

    override fun onPaymentFailure(message: String) {
        binding.purchasedMembershipPlanErrorTV.apply {
            visibility = View.VISIBLE
            text = if (message == "Credential attempts exceeded") {
                getString(R.string.unable_to_proceed)
            }else { message }
        }
    }

    override fun onSubmitClicked(membershipPlanModel: MembershipPlanModel) {
        membershipPlan = membershipPlanModel
        renewalType = Constants.NEW_SUBSCRIPTION
        getSubscriptionDetailsApi()
    }

}