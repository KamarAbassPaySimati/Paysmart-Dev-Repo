package com.afrimax.paysimati.data.model

data class PayMerchantModel(
    val amount: String,
    val enteredAmount:String,
    val txnFee: String,
    val vat: String,
    val paymaartId:String
)
