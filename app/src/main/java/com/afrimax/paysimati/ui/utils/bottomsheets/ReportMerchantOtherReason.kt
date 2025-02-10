package com.afrimax.paysimati.ui.utils.bottomsheets

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.afrimax.paysimati.R
import com.afrimax.paysimati.databinding.DeleteAccountOthersSheetBinding
import com.afrimax.paysimati.databinding.ReportMerchantOtherBinding
import com.afrimax.paysimati.ui.utils.interfaces.DeleteAccountInterface
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
            validateFieldForSubmit()

        }

        configureEditTextFocusListener()
       configureEditTextChangeListener()
        }

    private fun configureEditTextChangeListener() {
        val foucs =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
        val notInFocusDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_unfocused)
        binding.reportMerchantOthersSheetET.onFocusChangeListener=
            View.OnFocusChangeListener{ _,hasfoucs->
            if(hasfoucs) binding.reportMerchantOthersSheetET.background = foucs
            else binding.reportMerchantOthersSheetET.background = notInFocusDrawable
            }

    }

    private fun validateFieldForSubmit() {
        var valid = true
        if(binding.reportMerchantOthersSheetET.text.isEmpty()){
            valid = false
            binding.reportMerchantOthersSheetET.background=
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
        }
        if(valid){
            sheetcallback.onReportReasonTyped(binding.reportMerchantOthersSheetET.text.toString())
            dismiss()
        }
    }

    private fun configureEditTextFocusListener() {
        binding.reportMerchantOthersSheetET.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
            if(binding.reportMerchantOthersSheetET.text.isEmpty()){
                binding.reportMerchantOthersSheetET.background =
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_error
                    )
            }
                else{
                binding.reportMerchantOthersSheetET.background =
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_edit_text_focused
                    )
            }
            }

        })

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetcallback = context as ReportOtherReason
    }

//    override fun onDismiss(dialog: DialogInterface) {
//        super.onDismiss(dialog)
//        sheetcallback.onReportReasonTyped("")
//    }



    companion object {
        const val TAG = "ReportAccountOthersSheet"
    }
}