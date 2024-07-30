package com.afrimax.paymaart.ui.utils.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.model.CashOutRequestBody
import com.afrimax.paymaart.data.model.PayToAfrimaxRequestBody
import com.afrimax.paymaart.databinding.TotalAmountReceiptBottomSheetBinding
import com.afrimax.paymaart.ui.cashout.CashOutModel
import com.afrimax.paymaart.ui.paytoaffrimax.PayAfrimaxModel
import com.afrimax.paymaart.util.getFormattedAmount
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TotalReceiptSheet(private val model: Any) : BottomSheetDialogFragment() {
    private lateinit var b: TotalAmountReceiptBottomSheetBinding
    private var amount: String = "0.0"
    private var txnFee: String = "0.0"
    private var vat: String = "0.0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = TotalAmountReceiptBottomSheetBinding.inflate(inflater, container, false)
        when(model) {
            is PayAfrimaxModel -> {
                amount = model.amount
                txnFee = model.txnFee
                vat = model.vat
            }
            is CashOutModel ->{
                amount = model.displayAmount
                txnFee = model.transactionFee
                vat = model.vat
            }
        }
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
        var sendPaymentBottomSheet = SendPaymentBottomSheet()
        when(model) {
            is PayAfrimaxModel -> {
                val payToAfrimax = PayToAfrimaxRequestBody(
                    amount = model.amount.toDouble(),
                    customerName = model.afrimaxName,
                    customerId = model.afrimaxId.toInt(),
                    password = "",
                    paymaartId =  model.customerId,
                    paymaartName = model.customerName,
                    planName = model.planName
                )
                sendPaymentBottomSheet = SendPaymentBottomSheet(payToAfrimax)
            }
            is CashOutModel ->{
                val cashOutModel = CashOutRequestBody(
                    requestedTo = model.receiverPaymaartId,
                    transactionAmount = model.amount
                )
                sendPaymentBottomSheet = SendPaymentBottomSheet(cashOutModel)
            }
        }
        dismiss()
        sendPaymentBottomSheet.isCancelable = false
        sendPaymentBottomSheet.show(requireActivity().supportFragmentManager, SendPaymentBottomSheet.TAG)
    }

    companion object {
        const val TAG = "TotalReceiptSheet"
    }
}