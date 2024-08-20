package com.afrimax.paymaart.ui.payperson

data class PayPersonUnRegisteredModel(
    val amount: String,
    val txnFee: String,
    val vat: String,
    val receiverName: String,
    val phoneNumber:String,
    val note:String,
    val senderId:String
)
