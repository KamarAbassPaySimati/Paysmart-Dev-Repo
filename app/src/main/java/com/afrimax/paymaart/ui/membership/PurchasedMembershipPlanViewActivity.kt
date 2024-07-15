package com.afrimax.paymaart.ui.membership

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.ApiService
import com.afrimax.paymaart.data.model.SubscriptionDetailsRequestBody
import com.afrimax.paymaart.data.model.SubscriptionDetailsResponse
import com.afrimax.paymaart.databinding.ActivityPurchasedMembershipPlanViewBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.home.MembershipType
import com.afrimax.paymaart.ui.payment.PaymentSuccessfulActivity
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
        setUpView()
    }
    private fun setUpView(){
        membershipPlan = intent.parcelable<MembershipPlanModel>(Constants.MEMBERSHIP_MODEL)
        renewalType = intent.getStringExtra(Constants.RENEWAL_TYPE) ?: ""
        val membershipType = membershipPlan?.membershipType
        val autoRenewal = if (membershipPlan?.renewalType == true) { "On" } else "Off"
        val validity = membershipPlan?.validity
        val paymentType = membershipPlan?.paymentType
        val referenceNumber = membershipPlan?.referenceNumber
        val startDate = getStartDate()
        val endDate = getEndDate(startDate, validity)
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
        }

        binding.purchasedMembershipPlansSubmitButton.setOnClickListener {
            onSendPaymentClicked(
                SubscriptionDetailsRequestBody(
                    referenceNumber = referenceNumber,
                    subType = membershipType?.lowercase(),
                    autoRenew = membershipPlan?.renewalType == true
                )
            )
        }

        binding.addBankActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun onSendPaymentClicked(subscriptionDetailsRequestBody: SubscriptionDetailsRequestBody) {
        showButtonLoader()
        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val subscriptionDetails = ApiClient.apiService.getSubscriptionDetails(idToken, subscriptionDetailsRequestBody)

            subscriptionDetails.enqueue(object : Callback<SubscriptionDetailsResponse> {
                override fun onResponse(call: Call<SubscriptionDetailsResponse>, response: Response<SubscriptionDetailsResponse>, ) {
                    if (response.isSuccessful && response.body() != null) {
                        val totalAmountBottomSheet = TotalAmountReceiptBottomSheet(response.body()!!.subscriptionDetails, subscriptionDetailsRequestBody)
                        totalAmountBottomSheet.show(supportFragmentManager, TotalAmountReceiptBottomSheet.TAG)
                    }else{
                        runOnUiThread {
                            showToast(getString(R.string.default_error_toast))
                        }
                    }
                    hideButtonLoader()
                }

                override fun onFailure(p0: Call<SubscriptionDetailsResponse>, p1: Throwable) {
                    hideButtonLoader()
                    runOnUiThread {
                        showToast(getString(R.string.default_error_toast))
                    }
                }

            })
        }
    }

    private fun showButtonLoader() {
        binding.purchasedMembershipPlansSubmitButton.apply {
            text = ""
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

    private val dateFormat: SimpleDateFormat
        get() = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    override fun onPaymentSuccess() {
        val intent = Intent(this, PaymentSuccessfulActivity::class.java)
        val sceneTransitions = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
        startActivity(intent, sceneTransitions)
        finishAfterTransition()
    }

    override fun onPaymentFailure() {
        TODO("Not yet implemented")
    }

}