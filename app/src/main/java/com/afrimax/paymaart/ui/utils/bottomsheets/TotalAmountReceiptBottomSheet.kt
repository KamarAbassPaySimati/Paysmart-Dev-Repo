package com.afrimax.paymaart.ui.utils.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.TotalAmountReceiptBottomSheetBinding
import com.afrimax.paymaart.util.getFormattedAmount
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TotalAmountReceiptBottomSheet: BottomSheetDialogFragment() {
    private lateinit var binding: TotalAmountReceiptBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TotalAmountReceiptBottomSheetBinding.inflate(inflater, container, false)
        setupView()
        return binding.root
    }

    private fun setupView(){
        binding.totalAmountReceiptTotalAmount.text = getString(R.string.amount_formatted, getFormattedAmount("100".toDouble()))
        binding.totalAmountReceiptTxnFee.text = getString(R.string.amount_formatted, getFormattedAmount("100".toDouble()))
        binding.totalAmountReceiptVatIncluded.text = getString(R.string.amount_formatted, getFormattedAmount("0".toDouble()))

        binding.totalAmountReceiptProceed.setOnClickListener {
            dismiss()
            val sendPaymentBottomSheet = SendPaymentBottomSheet()
            sendPaymentBottomSheet.isCancelable = false
            sendPaymentBottomSheet.show(requireActivity().supportFragmentManager, SendPaymentBottomSheet.TAG)

        }
        binding.totalAmountReceiptSheetCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "TotalAmountReceipt"
    }


}