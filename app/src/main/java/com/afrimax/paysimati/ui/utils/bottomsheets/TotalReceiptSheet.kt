package com.afrimax.paysimati.ui.utils.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.model.CashOutRequestBody
import com.afrimax.paysimati.data.model.MerchantRequestPay
import com.afrimax.paysimati.data.model.PayMerchantModel
import com.afrimax.paysimati.data.model.PayMerchantRequest
import com.afrimax.paysimati.data.model.PayMerchantRequestModel
import com.afrimax.paysimati.data.model.PayToAfrimaxRequestBody
import com.afrimax.paysimati.data.model.PayToRegisteredPersonRequest
import com.afrimax.paysimati.data.model.PayToUnRegisteredPersonRequest
import com.afrimax.paysimati.databinding.TotalAmountReceiptBottomSheetBinding
import com.afrimax.paysimati.ui.cashout.CashOutModel
import com.afrimax.paysimati.ui.payperson.PayPersonRegisteredModel
import com.afrimax.paysimati.ui.payperson.PayPersonUnRegisteredModel
import com.afrimax.paysimati.ui.paytoaffrimax.PayAfrimaxModel
import com.afrimax.paysimati.util.getFormattedAmount
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TotalReceiptSheet(private val model: Any) : BottomSheetDialogFragment() {
    private lateinit var b: TotalAmountReceiptBottomSheetBinding
    private var totalAmount: String = "0.0"
    private  var enteredAmount:String ="0.0"
    private var txnFee: String = "0.0"
    private var vat: String = "0.0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = TotalAmountReceiptBottomSheetBinding.inflate(inflater, container, false)
        when (model) {
            is PayAfrimaxModel -> {
                totalAmount = model.amount
                enteredAmount = model.enteredAmount
                txnFee = model.txnFee
                vat = model.vat
            }

            is CashOutModel -> {
                totalAmount = model.displayAmount
                enteredAmount = model.enteredAmount
                txnFee = model.transactionFee
                vat = model.vat
            }

            is PayPersonUnRegisteredModel -> {
                totalAmount = model.amount
                enteredAmount = model.enteredAmount
                vat = model.vat
                txnFee = model.txnFee
            }

            is PayPersonRegisteredModel -> {
                vat = model.vat
                enteredAmount = model.enteredAmount
                txnFee = model.txnFee
                totalAmount = model.amount
            }
            is PayMerchantModel->{
                vat = model.vat
                enteredAmount = model.amount
                txnFee = model.txnFee
                totalAmount = model.amount
            }
            is  PayMerchantRequestModel->{
                vat = model.vat
                enteredAmount = model.amount
                txnFee = model.txnFee
                totalAmount = model.amount
            }

        }
        b.totalAmountReceiptTotalAmount.text =
            getString(R.string.amount_formatted, getFormattedAmount(totalAmount))
        b.totalAmountReceiptTxnFee.text =
            getString(R.string.amount_formatted, getFormattedAmount(txnFee))
        b.totalAmountReceiptVatIncluded.text =
            getString(R.string.amount_formatted, getFormattedAmount(vat))

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
        when (model) {
            is PayAfrimaxModel -> {
                val payToAfrimax = PayToAfrimaxRequestBody(
                    amount = model.enteredAmount.toDouble(),
                    customerName = model.afrimaxName,
                    customerId = model.afrimaxId.toInt(),
                    password = "",
                    paymaartId = model.customerId,
                    paymaartName = model.customerName,
                    planName = model.planName
                )
                sendPaymentBottomSheet = SendPaymentBottomSheet(payToAfrimax)
            }

            is CashOutModel -> {
                val cashOutModel = CashOutRequestBody(
                    requestedTo = model.receiverPaymaartId, transactionAmount = model.enteredAmount
                )
                sendPaymentBottomSheet = SendPaymentBottomSheet(cashOutModel)
            }

            is PayPersonUnRegisteredModel -> {
                val payPersonUnRegisteredModel = PayToUnRegisteredPersonRequest(
                    amount = model.enteredAmount.toDouble(),
                    callType = false,
                    phoneNumber = model.phoneNumber,
                    receiverName = model.receiverName,
                    note = model.note,
                    senderId = model.senderId,
                    password = null
                )
                sendPaymentBottomSheet = SendPaymentBottomSheet(payPersonUnRegisteredModel)
            }

            is PayPersonRegisteredModel -> {
                val payPersonRegisteredModel = PayToRegisteredPersonRequest(
                    transactionAmount = model.enteredAmount.toDouble(),
                    paymaartId = model.paymaartId,
                    note = model.note,
                    credential = null
                )
                sendPaymentBottomSheet = SendPaymentBottomSheet(payPersonRegisteredModel)
            }
            is PayMerchantModel->{
                val payMerchantModel = PayMerchantRequest(
                    amount = model.amount.toDouble(),
                    receiverId = model.recieiverid,
                    flag=false,
                    password = null,
                    senderId = model.senderId,
                    entryBy = model.entryBy,
                    note = model.note,
                    tillnumber = model.tillnumber
                )

                sendPaymentBottomSheet = SendPaymentBottomSheet(payMerchantModel)

            }
            is PayMerchantRequestModel ->{
                val payMerchantRequestModel = MerchantRequestPay(
                    amount = model.amount.toDouble(),
                    requestId = model.requestid,
                    receiverId = model.recieiverid,
                    flag=false,
                    password = null,
                    senderId = model.senderId,
                    entryBy = model.entryBy,
                    note = model.note
                )

                sendPaymentBottomSheet = SendPaymentBottomSheet(payMerchantRequestModel)

            }
        }
        dismiss()
        sendPaymentBottomSheet.isCancelable = false
        sendPaymentBottomSheet.show(
            requireActivity().supportFragmentManager, SendPaymentBottomSheet.TAG
        )
    }

    companion object {
        const val TAG = "TotalReceiptSheet"
    }
}