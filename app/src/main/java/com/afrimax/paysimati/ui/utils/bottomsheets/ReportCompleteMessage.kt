package com.afrimax.paysimati.ui.utils.bottomsheets

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paysimati.databinding.MerchantReportedBinding
import com.afrimax.paysimati.ui.paymerchant.MerchantProfile
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReportCompleteMessage: BottomSheetDialogFragment() {
    private lateinit var  binding : MerchantReportedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = MerchantReportedBinding.inflate(inflater, container, false)
        setUpListeners()

        return binding.root
    }

    private fun setUpListeners() {
        binding.sheetcomletedMerchantdone.setOnClickListener{
            dismiss()

//            startActivity(Intent(requireActivity(), MerchantProfile::class.java))
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        requireActivity().onBackPressedDispatcher.onBackPressed() // Trigger back press
    }
    companion object{
        const val TAG ="completereportMerchant"
    }

}