package com.afrimax.paymaart.ui.utils.bottomsheets

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.os.Bundle
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
import com.afrimax.paymaart.ui.utils.interfaces.MembershipPlansInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MembershipPlansPurchaseBottomSheet(private val membershipType: MembershipType): BottomSheetDialogFragment() {
    private lateinit var binding: MembershipPlansPurchaseBottomSheetBinding
    private lateinit var sheetCallBack: MembershipPlansInterface
    private lateinit var renewalType: String
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
        val renewalTypeList = listOf(R.string.thirty_days_for_300_mwk, R.string.ninety_one_days_for_850_mwk)
        when (membershipType) {
            MembershipType.PRIME -> binding.membershipPlansMembershipType.text = getString(R.string.prime)
            MembershipType.PRIMEX -> binding.membershipPlansMembershipType.text = getString(R.string.primeX)
            MembershipType.GO -> binding.membershipPlansMembershipType.text = getString(R.string.go)
        }
        generateRadioButtonOptions(renewalTypeList)

        binding.membershipPlansCloseButton.setOnClickListener {
            dismiss()
        }

        binding.membershipPlansSubmitButton.setOnClickListener {
            dismiss()
        }

        binding.membershipPlansSubmitButton.setOnClickListener {
            dismiss()
            sheetCallBack.onSubmitClicked(renewalType)
        }

    }

    private fun generateRadioButtonOptions(renewalTypeList: List<Int>){
        binding.membershipPlansRadioGroup.orientation = LinearLayout.VERTICAL
        val num = renewalTypeList.size - 1
        val fontStyle = ResourcesCompat.getFont(requireContext(), R.font.inter_regular)
        val mLayoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f)
        val buttonTint = ContextCompat.getColorStateList(requireContext(), R.color.primaryColor)
        for (i in 0..num){
            val radioButton= AppCompatRadioButton(requireContext())
            radioButton.apply {
                buttonTintList = buttonTint
                id = i
                setText(renewalTypeList[i])
                typeface = fontStyle
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setPadding(0, 10, 0, 10)
                setOnClickListener{view ->
                    renewalType = getString(renewalTypeList[view.id])
                }
                layoutParams = mLayoutParams
            }
            binding.membershipPlansRadioGroup.addView(radioButton)
        }
        if (renewalTypeList.isNotEmpty()){
            val firstButton = binding.membershipPlansRadioGroup.getChildAt(0) as AppCompatRadioButton
            firstButton.isChecked = true
            renewalType = getString(renewalTypeList[0])
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallBack = context as MembershipPlansInterface
    }
    companion object{
        const val TAG = "MembershipPlansPurchaseBottomSheet"
    }
}