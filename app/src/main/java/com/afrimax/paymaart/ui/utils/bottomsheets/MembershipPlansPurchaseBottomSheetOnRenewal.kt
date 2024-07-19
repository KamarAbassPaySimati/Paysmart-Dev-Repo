package com.afrimax.paymaart.ui.utils.bottomsheets

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.MembershipPlansPurchaseBottomSheetBinding
import com.afrimax.paymaart.ui.home.MembershipType
import com.afrimax.paymaart.ui.membership.MembershipPlanModel
import com.afrimax.paymaart.ui.membership.PaymentType
import com.afrimax.paymaart.ui.membership.RenewalPlans
import com.afrimax.paymaart.ui.utils.interfaces.MembershipPlansInterface
import com.afrimax.paymaart.ui.utils.interfaces.SendPaymentInterface
import com.afrimax.paymaart.util.showLogE
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MembershipPlansPurchaseBottomSheetOnRenewal(private val membershipType: MembershipType, private val planTypes: List<RenewalPlans>): BottomSheetDialogFragment() {
    private lateinit var binding: MembershipPlansPurchaseBottomSheetBinding
    private lateinit var sheetCallBack: SendPaymentInterface
    private lateinit var membershipPlanModel: MembershipPlanModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MembershipPlansPurchaseBottomSheetBinding.inflate(inflater, container, false)
        setUpView(membershipType = membershipType)
        return binding.root
    }

    private fun setUpView(membershipType: MembershipType){
        when (membershipType) {
            MembershipType.PRIME -> binding.membershipPlansMembershipType.text = getString(R.string.prime)
            MembershipType.PRIMEX -> binding.membershipPlansMembershipType.text = getString(R.string.primeX)
            MembershipType.GO -> binding.membershipPlansMembershipType.text = getString(R.string.go)
        }
        generateRadioButtonOptions(planTypes)

        binding.membershipPlansCloseButton.setOnClickListener {
            dismiss()
        }

        binding.membershipPlansSubmitButton.setOnClickListener {
            onSubmitClicked()
        }

    }

    private fun generateRadioButtonOptions(renewalTypeList: List<RenewalPlans>) {
        binding.membershipPlansRadioGroup.orientation = LinearLayout.VERTICAL
        val num = renewalTypeList.size - 1
        val fontStyle = ResourcesCompat.getFont(requireContext(), R.font.inter_regular)
        val mLayoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f)
        val buttonTint = ContextCompat.getColorStateList(requireContext(), R.color.primaryColor)
        for (i in 0..num) {
            val radioButton = AppCompatRadioButton(requireContext())
            radioButton.apply {
                buttonTintList = buttonTint
                id = i
                text = getString(R.string.renewal_message_formatted, renewalTypeList[i].planValidity, renewalTypeList[i].planPrice)
                typeface = fontStyle
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setPadding(0, 10, 0, 10)
                setOnClickListener { view ->
                    membershipPlanModel = MembershipPlanModel(
                        membershipType = renewalTypeList[view.id].membershipType,
                        validity = renewalTypeList[view.id].planValidity,
                        referenceNumber = renewalTypeList[view.id].referenceNumber,
                        paymentType = PaymentType.PREPAID.type,
                    )
                }
                    layoutParams = mLayoutParams
                }
            binding.membershipPlansRadioGroup.addView(radioButton)
        }
        if (renewalTypeList.isNotEmpty()) {
            val firstButton = binding.membershipPlansRadioGroup.getChildAt(0) as AppCompatRadioButton
            firstButton.isChecked = true
            membershipPlanModel = MembershipPlanModel(
                membershipType = renewalTypeList[0].membershipType,
                validity = renewalTypeList[0].planValidity,
                referenceNumber = renewalTypeList[0].referenceNumber,
                paymentType = PaymentType.PREPAID.type,
            )
        }
    }

    private fun onSubmitClicked(){
        dismiss()
        Handler(Looper.getMainLooper()).postDelayed({
            sheetCallBack.onSubmitClicked(membershipPlanModel.copy(renewalType = isAutoRenewal))
        }, 250)

    }

    private val isAutoRenewal: Boolean
        get() = binding.membershipPlansAutoRenewalSwitch.isChecked

    private fun showButtonLoader(){
        binding.membershipPlansSubmitButtonLoaderLottie.visibility = View.VISIBLE
        binding.membershipPlansSubmitButton.apply {
            text = ""
            isEnabled = false
        }
        isCancelable = false
        binding.membershipPlansCloseButton.isEnabled = false
    }

    private fun hideButtonLoader(){
        binding.membershipPlansSubmitButtonLoaderLottie.visibility = View.GONE
        binding.membershipPlansSubmitButton.apply {
            text = getString(R.string.submit)
            isEnabled = true
        }
        binding.membershipPlansCloseButton.isEnabled = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallBack = context as SendPaymentInterface
    }
    companion object{
        const val TAG = "MembershipPlansPurchaseBottomSheetOnRenewal"
    }
}