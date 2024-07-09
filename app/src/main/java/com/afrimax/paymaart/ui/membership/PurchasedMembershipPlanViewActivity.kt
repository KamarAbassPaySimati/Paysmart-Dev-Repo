package com.afrimax.paymaart.ui.membership

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.ActivityPurchasedMembershipPlanViewBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.home.MembershipType
import com.afrimax.paymaart.ui.utils.bottomsheets.TotalAmountReceiptBottomSheet
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.getDrawableExt

class PurchasedMembershipPlanViewActivity : BaseActivity() {
    private lateinit var binding: ActivityPurchasedMembershipPlanViewBinding
    private lateinit var membershipValidityType: String
    private lateinit var membershipType: String
    private var autoRenewal: Boolean = false
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
        membershipValidityType = intent.getStringExtra(Constants.MEMBERSHIP_VALIDITY_TYPE) ?: ""
        membershipType = intent.getStringExtra(Constants.MEMBERSHIP_TYPE) ?: ""
        autoRenewal = intent.getBooleanExtra(Constants.AUTO_RENEWAL, false)

        when (membershipType) {
            MembershipType.PRIME.type -> {
                val primeColor = getColor(R.color.primeMembershipGrey)
                binding.purchasedMembershipPlanMembershipType.apply {
                    text = getString(R.string.membership_type_formatted, "Prime")
                    setTextColor(primeColor)
                }
                binding.purchasedMembershipPlanOptions.apply{
                    text = getString(R.string.membership_plan_formatted, "30 Days", "Prepaid", "Auto Renewal")
                    setTextColor(primeColor)
                }
                binding.purchasedMembershipPlanValidity.apply{
                    text = getString(R.string.membership_plan_validity_formatted, "15 Jun 2024", "14 Jul 2024")
                    setTextColor(primeColor)
                }
                binding.purchasedMembershipPlanImageBg.background = this.getDrawableExt(R.drawable.ic_prime_membership_card_bg)
            }
            MembershipType.PRIMEX.type -> {
                val primeXColor = getColor(R.color.primeXCardBackground)
                binding.purchasedMembershipPlanMembershipType.apply {
                    text = getString(R.string.membership_type_formatted, "PrimeX")
                    setTextColor(primeXColor)
                }
                binding.purchasedMembershipPlanOptions.apply{
                    text = getString(R.string.membership_plan_formatted, "30 Days", "Prepaid", "Auto Renewal")
                    setTextColor(primeXColor)
                }
                binding.purchasedMembershipPlanValidity.apply{
                    text = getString(R.string.membership_plan_validity_formatted, "15 Jun 2024", "14 Jul 2024")
                    setTextColor(primeXColor)
                }
                binding.purchasedMembershipPlanImageBg.background = this.getDrawableExt(R.drawable.ic_prime_x_membership_card_bg)
            }
            MembershipType.GO.type-> {
                //This scenario won't happen
                val goColor = getColor(R.color.goMemberStrokeColor)
                binding.purchasedMembershipPlanMembershipType.apply {
                    text = getString(R.string.membership_type_formatted, "Go")
                    setTextColor(goColor)
                }
                binding.purchasedMembershipPlanOptions.apply{
                    text = getString(R.string.membership_plan_formatted, "30 Days", "Prepaid", "Auto Renewal")
                    setTextColor(goColor)
                }
                binding.purchasedMembershipPlanValidity.apply{
                    text = getString(R.string.membership_plan_validity_formatted, "15 Jun 2024", "14 Jul 2024")
                    setTextColor(goColor)
                }
                binding.purchasedMembershipPlanImageBg.background = this.getDrawableExt(R.drawable.ic_go_membership_card_bg)
            }
        }

        binding.purchasedMembershipPlansSubmitButton.setOnClickListener {
            val totalAmountBottomSheet = TotalAmountReceiptBottomSheet()
            totalAmountBottomSheet.show(supportFragmentManager, TotalAmountReceiptBottomSheet.TAG)
        }
    }

}