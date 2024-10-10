package com.afrimax.paysimati.ui.utils.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.model.SubscriptionDetails
import com.afrimax.paysimati.data.model.SubscriptionDetailsRequestBody
import com.afrimax.paysimati.databinding.TotalAmountReceiptBottomSheetBinding
import com.afrimax.paysimati.util.getFormattedAmount
import com.afrimax.paysimati.util.showLogE
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TotalAmountReceiptBottomSheet(private val subscriptionDetails: SubscriptionDetails, private val subscriptionDetailsRequestBody: SubscriptionDetailsRequestBody): BottomSheetDialogFragment() {
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
        binding.totalAmountReceiptTotalAmount.text = getString(R.string.amount_formatted, getFormattedAmount(subscriptionDetails.totalAmount))
        binding.totalAmountReceiptTxnFee.text = getString(R.string.amount_formatted, getFormattedAmount(subscriptionDetails.transactionFee))
        binding.totalAmountReceiptVatIncluded.text = getString(R.string.amount_formatted, getFormattedAmount(subscriptionDetails.vat))

        binding.totalAmountReceiptProceed.setOnClickListener {
            dismiss()
            val sendPaymentBottomSheet = SendPaymentBottomSheet(subscriptionDetailsRequestBody)
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