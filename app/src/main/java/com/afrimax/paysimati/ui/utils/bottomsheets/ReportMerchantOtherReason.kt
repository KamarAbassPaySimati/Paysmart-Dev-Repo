package com.afrimax.paysimati.ui.utils.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paysimati.databinding.DeleteAccountOthersSheetBinding
import com.afrimax.paysimati.databinding.ReportMerchantOtherBinding
import com.afrimax.paysimati.ui.utils.interfaces.ReportOtherReason
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReportMerchantOtherReasons: BottomSheetDialogFragment() {
    private lateinit var binding: ReportMerchantOtherBinding
    private lateinit var  sheetcallback : ReportOtherReason

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ReportMerchantOtherBinding.inflate(inflater,container,false)

        setUpListeners()

        return binding.root
    }

    private fun setUpListeners() {
        binding.reportMerchantOthersSheetCloseButton.setOnClickListener {
            dismiss()
        }
        binding.reportMerchantOthersSheetSubmitButton.setOnClickListener {

        }

        configureEditTextFocusListener()
        //configureEditTextChangeListener()
        }

    private fun configureEditTextFocusListener() {

    }

}
