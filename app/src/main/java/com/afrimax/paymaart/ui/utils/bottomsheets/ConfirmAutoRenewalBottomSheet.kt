package com.afrimax.paymaart.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paymaart.databinding.ConfirmAutoRenewalBottomSheetBinding
import com.afrimax.paymaart.ui.utils.interfaces.MembershipPlansInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ConfirmAutoRenewalBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: ConfirmAutoRenewalBottomSheetBinding
    private lateinit var sheetCallback: MembershipPlansInterface
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ConfirmAutoRenewalBottomSheetBinding.inflate(inflater, container, false)
        setupView()
        return binding.root
    }

    private fun setupView() {
        binding.confirmAutoRenewalConfirmButton.setOnClickListener {
            onConfirmClicked()
        }

        binding.confirmAutoRenewalCancelButton.setOnClickListener{
            dismiss()
            sheetCallback.onCancelClicked()
        }
    }

    private fun onConfirmClicked() {
        dismiss()
        sheetCallback.onConfirm()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as MembershipPlansInterface
    }

    companion object{
        const val TAG = "ConfirmAutoRenewalBottomSheet"
    }
}