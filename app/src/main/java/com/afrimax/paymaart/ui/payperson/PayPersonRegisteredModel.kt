package com.afrimax.paymaart.ui.payperson

data class PayPersonRegisteredModel(
    val amount: String,
    val txnFee: String,
    val vat: String,
    val note:String,
    val paymaartId:String
)
