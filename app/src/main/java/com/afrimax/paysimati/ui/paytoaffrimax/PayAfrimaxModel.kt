package com.afrimax.paysimati.ui.paytoaffrimax

data class PayAfrimaxModel(
    val amount: String,
    val enteredAmount:String,
    val txnFee: String,
    val vat: String,
    val afrimaxId: String,
    val afrimaxName: String,
    val customerName: String,
    val customerId: String,
    val planName: String? = null
)
