package com.afrimax.paysimati.data.model

data class PayMerchantRequestModel(
    val amount: String,
    val txnFee: String,
    val vat: String,
    val recieiverid:String,
    val requestid :String,
    val note:String,
    val senderId:String,
    val entryBy: String,
    val tillnumber: String? = null
)
