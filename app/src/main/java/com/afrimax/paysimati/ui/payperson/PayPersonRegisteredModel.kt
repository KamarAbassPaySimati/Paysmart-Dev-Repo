package com.afrimax.paysimati.ui.payperson

data class PayPersonRegisteredModel(
    val amount: String,
    val enteredAmount:String,
    val txnFee: String,
    val vat: String,
    val note:String,
    val paymaartId:String
)
