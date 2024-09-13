package com.afrimax.paysimati.ui.membership

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.MembershipPlan
import com.afrimax.paysimati.data.model.MembershipPlansResponse
import com.afrimax.paysimati.data.model.MembershipUserData
import com.afrimax.paysimati.databinding.ActivityMembershipPlansBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.home.MembershipType
import com.afrimax.paysimati.ui.utils.adapters.MembershipPlanAdapter
import com.afrimax.paysimati.ui.utils.bottomsheets.ConfirmAutoRenewalBottomSheet
import com.afrimax.paysimati.ui.utils.bottomsheets.MembershipPlansPurchaseBottomSheet
import com.afrimax.paysimati.ui.utils.interfaces.MembershipPlansInterface
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.showLogE
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Duration
import java.time.Instant
import java.util.Date
import java.util.concurrent.TimeUnit

class MembershipPlansActivity : BaseActivity(), MembershipPlansInterface {
    private lateinit var binding: ActivityMembershipPlansBinding
    private lateinit var membershipPlanAdapter: MembershipPlanAdapter
    private var planList: MutableList<MembershipPlan> = mutableListOf()
    private var planTypes: List<MembershipPlanRenewalType> = emptyList()
    private var displayType: String = ""
    private var membershipType: String = ""
    private var initialPrimeSwitchPosition: Boolean = false
    private var initialPrimeXSwitchPosition: Boolean = false
    private lateinit var membershipPlanModel: MembershipPlanModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembershipPlansBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activityMembershipPlan)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true
        setUpView()
        setUpRecyclerView()
    }

    private fun setUpView(){
        displayType = intent.getStringExtra(Constants.DISPLAY_TYPE) ?: ""
        membershipType = intent.getStringExtra(Constants.MEMBERSHIP_TYPE) ?: ""
        if (displayType == Constants.HOME_SCREEN_BANNER) {
            setEntryAnimation()
        }
        binding.membershipPlansBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.buyButtonPrime.setOnClickListener {
            val primePlanList: MutableList<RenewalPlans> = mutableListOf()
            planTypes.forEach { plan ->
                primePlanList.add(RenewalPlans(membershipType = MembershipType.PRIME.type,referenceNumber = plan.referenceNumber, planPrice = plan.primePrice, planValidity = plan.validDate))
            }
            val membershipPurchasePlansSheet = MembershipPlansPurchaseBottomSheet(MembershipType.PRIME, primePlanList)
            membershipPurchasePlansSheet.isCancelable = false
            membershipPurchasePlansSheet.show(supportFragmentManager, MembershipPlansPurchaseBottomSheet.TAG)
        }

        binding.buyButtonPrimeX.setOnClickListener {
            val primeXPlanList: MutableList<RenewalPlans> = mutableListOf()
            planTypes.forEach { plan ->
                primeXPlanList.add(RenewalPlans(membershipType = MembershipType.PRIMEX.type,referenceNumber = plan.referenceNumber, planPrice = plan.primeXPrice, planValidity = plan.validDate))
            }
            val membershipPurchasePlansSheet = MembershipPlansPurchaseBottomSheet(MembershipType.PRIMEX, primeXPlanList)
            membershipPurchasePlansSheet.isCancelable = false
            membershipPurchasePlansSheet.show(supportFragmentManager, MembershipPlansPurchaseBottomSheet.TAG)
        }

        binding.buyButtonPrimeSwitch.setOnCheckedChangeListener { button, isChecked ->
            button.alpha = 1f
            button.setOnClickListener {
                if (!isChecked) {
                    val confirmAutoRenewalBottomSheet = ConfirmAutoRenewalBottomSheet(MembershipType.PRIME, false)
                    confirmAutoRenewalBottomSheet.isCancelable = false
                    confirmAutoRenewalBottomSheet.show(supportFragmentManager, ConfirmAutoRenewalBottomSheet.TAG)
                }else {
                    val primePlanList: MutableList<RenewalPlans> = mutableListOf()
                    planTypes.forEach { plan ->
                        "ResponsePrimeReference".showLogE(plan.referenceNumber)
                        primePlanList.add(RenewalPlans(membershipType = MembershipType.PRIME.type,referenceNumber = plan.referenceNumber, planPrice = plan.primePrice, planValidity = plan.validDate))
                    }
                    "Response".showLogE(membershipPlanModel)
                    val intent = Intent(this, PurchasedMembershipPlanViewActivity::class.java)
                    intent.putParcelableArrayListExtra(Constants.MEMBERSHIP_PLANS, ArrayList(primePlanList))
                    intent.putExtra(Constants.MEMBERSHIP_MODEL, membershipPlanModel as Parcelable)
                    intent.putExtra(Constants.RENEWAL_TYPE, Constants.AUTO_RENEWAL_OFF)
                    startActivity(intent)
                }
            }
        }

        binding.buyButtonPrimeXSwitch.setOnCheckedChangeListener { button, isChecked ->
            if (button.isChecked) button.alpha = 1f
            button.setOnClickListener {
                if (!isChecked) {
                    val confirmAutoRenewalBottomSheet = ConfirmAutoRenewalBottomSheet(MembershipType.PRIMEX, false)
                    confirmAutoRenewalBottomSheet.isCancelable = false
                    confirmAutoRenewalBottomSheet.show(supportFragmentManager, ConfirmAutoRenewalBottomSheet.TAG)
                }else {
                    val primeXPlanList: MutableList<RenewalPlans> = mutableListOf()
                    planTypes.forEach { plan ->
                        primeXPlanList.add(RenewalPlans(membershipType = MembershipType.PRIMEX.type,referenceNumber = plan.referenceNumber, planPrice = plan.primeXPrice, planValidity = plan.validDate))
                    }
                    "Response".showLogE(primeXPlanList)
                    val intent = Intent(this, PurchasedMembershipPlanViewActivity::class.java)
                    intent.putParcelableArrayListExtra(Constants.MEMBERSHIP_PLANS, ArrayList(primeXPlanList))
                    intent.putExtra(Constants.MEMBERSHIP_MODEL, membershipPlanModel as Parcelable)
                    intent.putExtra(Constants.RENEWAL_TYPE, Constants.AUTO_RENEWAL_OFF)
                    startActivity(intent)
                }
            }
        }
    }

    private fun setUpRecyclerView(){
        membershipPlanAdapter = MembershipPlanAdapter(planList)
        binding.membershipPlansRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MembershipPlansActivity, LinearLayoutManager.VERTICAL, false)
            adapter = membershipPlanAdapter
        }

        setMembershipBannerVisibility(false)
    }

    private fun setMembershipButtons(userData: MembershipUserData?) {
        if ( userData != null ){
            when(userData.membership) {
                MembershipType.PRIME.type -> {
                    binding.buyButtonPrime.visibility = View.GONE
                    binding.buyButtonPrimeSwitchContainer.visibility = View.VISIBLE
                    if (userData.autoRenew) {
                        binding.buyButtonPrimeSwitch.isChecked = true
                    }else{
                        binding.buyButtonPrimeSwitch.isChecked = false
                        binding.buyButtonPrimeSwitch.alpha = .2f
                    }
                    initialPrimeSwitchPosition = userData.autoRenew
                }
                MembershipType.PRIMEX.type -> {
                    binding.buyButtonPrimeX.visibility = View.GONE
                    binding.buyButtonPrimeXSwitchContainer.visibility = View.VISIBLE
                    if (userData.autoRenew) {
                        binding.buyButtonPrimeXSwitch.isChecked = true
                    }else{
                        binding.buyButtonPrimeXSwitch.isChecked = false
                        binding.buyButtonPrimeXSwitch.alpha = .2f
                    }
                    initialPrimeXSwitchPosition = userData.autoRenew
                }
            }
            membershipPlanModel = MembershipPlanModel(
                membershipType = userData.membership,
                validity = getValidity(userData.membershipStart, userData.membershipExpiry),
                paymentType = PaymentType.PREPAID.type,
                renewalType = userData.autoRenew,
                referenceNumber = userData.membershipId
            )
        }
    }

    private fun populateMemberShipPlan(){
        showLoader()
        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val membershipPlansResponse = ApiClient.apiService.getMembershipDetails(idToken)
            membershipPlansResponse.enqueue(object: Callback<MembershipPlansResponse>{
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<MembershipPlansResponse>, response: Response<MembershipPlansResponse>, ) {
                    hideLoader()
                    val body = response.body()
                    if (response.isSuccessful && body != null){
                        planTypes = parsePlans(body.membershipPlans)
                        setMembershipButtons(body.userData)
                        planList.addAll(body.membershipPlans)
                        binding.membershipPlansRecyclerView.adapter?.notifyDataSetChanged()
                    } else {
                        runOnUiThread {
                            showToast(getString(R.string.default_error_toast))
                        }
                    }
                }

                override fun onFailure(call: Call<MembershipPlansResponse>, throwable: Throwable) {
                    hideLoader()
                    runOnUiThread {
                        showToast(getString(R.string.default_error_toast))
                    }
                }
            })
        }
    }

    private fun parsePlans(membershipPlans: List<MembershipPlan>): List<MembershipPlanRenewalType>{
        val membershipPlanRenewalTypes: MutableList<MembershipPlanRenewalType> = mutableListOf()
        membershipPlans.forEach { plan ->
            plan.serviceBeneficiary?.let { serviceBeneficiary ->
                if (serviceBeneficiary == Constants.MEMBERSHIP) {
                    membershipPlanRenewalTypes.add(
                        MembershipPlanRenewalType(
                            validityDay = plan.subtitle,
                            primePrice = plan.prime,
                            primeXPrice = plan.primeX,
                            referenceNumber = plan.referenceNumber ?: ""
                        )
                    )
                }
            }

        }
        return membershipPlanRenewalTypes
    }

    private fun getValidity(startDate: Long, expiryDate: Long): String{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val instant1 = Instant.ofEpochSecond(startDate)
            val instant2 = Instant.ofEpochSecond(expiryDate)
            val duration = Duration.between(instant1, instant2)
            duration.toDays().toString()
        }else {
            val timestamp1 = startDate * 1000
            val timestamp2 = expiryDate * 1000
            val date1 = Date(timestamp1)
            val date2 = Date(timestamp2)
            val diffInMillies = date2.time - date1.time
            TimeUnit.MILLISECONDS.toDays(diffInMillies).toString()
        }
    }

    private fun showLoader() {
        binding.membershipPlansRecyclerView.visibility = View.GONE
        binding.membershipPlansHeader.visibility = View.GONE
        binding.membershipPlansFooter.visibility = View.GONE
        binding.membershipPlanLoaderLottie.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideLoader() {
        binding.membershipPlansRecyclerView.visibility = View.VISIBLE
        binding.membershipPlansHeader.visibility = View.VISIBLE
        binding.membershipPlansFooter.visibility = View.VISIBLE
        binding.membershipPlanLoaderLottie.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun setEntryAnimation() {
        val slide = Slide()
        slide.slideEdge = Gravity.BOTTOM
        slide.setDuration(200)
        slide.setInterpolator(AccelerateInterpolator())
        window.enterTransition = slide
        window.returnTransition = slide
    }

    override fun onResume() {
        super.onResume()
        populateMemberShipPlan()
    }

    override fun onSubmitClicked(membershipPlanModel: MembershipPlanModel) {
        val intent = Intent(this, PurchasedMembershipPlanViewActivity::class.java)
        intent.putExtra(Constants.MEMBERSHIP_MODEL, membershipPlanModel as Parcelable)
        intent.putExtra(Constants.RENEWAL_TYPE, Constants.NEW_SUBSCRIPTION)
        startActivity(intent)
    }

    override fun onConfirm(membershipType: MembershipType) {
        populateMemberShipPlan()
    }

    override fun onCancelClicked(membershipType: MembershipType) {
        when( membershipType ) {
            MembershipType.PRIME -> {
                binding.buyButtonPrimeSwitch.isChecked = initialPrimeSwitchPosition
            }
            MembershipType.PRIMEX -> {
                binding.buyButtonPrimeXSwitch.isChecked = initialPrimeSwitchPosition
            }
            else -> {}
        }
    }
}