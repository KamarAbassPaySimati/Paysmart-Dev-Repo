package com.afrimax.paymaart.ui.utils.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.model.SubscriptionDetails
import com.afrimax.paymaart.data.model.SubscriptionDetailsRequestBody
import com.afrimax.paymaart.databinding.TotalAmountReceiptBottomSheetBinding
import com.afrimax.paymaart.util.getFormattedAmount
import com.afrimax.paymaart.util.showLogE
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