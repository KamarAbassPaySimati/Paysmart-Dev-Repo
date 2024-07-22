package com.afrimax.paymaart.ui.utils.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.model.PayToAfrimaxRequestBody
import com.afrimax.paymaart.databinding.TotalAmountReceiptBottomSheetBinding
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.getFormattedAmount
import com.afrimax.paymaart.util.showLogE
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TotalReceiptSheet : BottomSheetDialogFragment() {
    private lateinit var b: TotalAmountReceiptBottomSheetBinding
    private lateinit var amount: String
    private lateinit var afrimaxId: String
    private lateinit var afrimaxName: String
    private lateinit var customerName: String
    private lateinit var customerId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = TotalAmountReceiptBottomSheetBinding.inflate(inflater, container, false)
        amount = requireArguments().getString(Constants.PAYMENT_AMOUNT) ?: ""
        val txnFee = requireArguments().getString(Constants.PAYMENT_TXN_FEE) ?: ""
        val vat = requireArguments().getString(Constants.PAYMENT_VAT) ?: ""
        afrimaxId = requireArguments().getString(Constants.AFRIMAX_ID) ?: ""
        afrimaxName = requireArguments().getString(Constants.AFRIMAX_NAME) ?: ""
        customerName = requireArguments().getString(Constants.CUSTOMER_NAME) ?: ""
        customerId = requireArguments().getString(Constants.CUSTOMER_ID) ?: ""

        b.totalAmountReceiptTotalAmount.text = getString(R.string.amount_formatted, getFormattedAmount(amount))
        b.totalAmountReceiptTxnFee.text = getString(R.string.amount_formatted, getFormattedAmount(txnFee))
        b.totalAmountReceiptVatIncluded.text = getString(R.string.amount_formatted, getFormattedAmount(vat))

        initViews()
        setUpListeners()

        return b.root
    }

    private fun initViews() {
        //
    }

    private fun setUpListeners() {
        b.totalAmountReceiptProceed.setOnClickListener {
            dismiss()
            onClickProceed()
        }

        b.totalAmountReceiptSheetCancel.setOnClickListener {
            dismiss()
        }

    }

    private fun onClickProceed() {
        val payToAfrimax = PayToAfrimaxRequestBody(
            amount = amount.toDouble(),
            customerName = afrimaxName,
            customerId = afrimaxId.toInt(),
            password = "",
            paymaartId =  customerId,
            paymaartName = customerName
        )
        val sendPaymentBottomSheet = SendPaymentBottomSheet(payToAfrimax)
        dismiss()
        sendPaymentBottomSheet.isCancelable = false
        sendPaymentBottomSheet.show(requireActivity().supportFragmentManager, SendPaymentBottomSheet.TAG)
    }

    companion object {
        const val TAG = "TotalReceiptSheet"
    }
}