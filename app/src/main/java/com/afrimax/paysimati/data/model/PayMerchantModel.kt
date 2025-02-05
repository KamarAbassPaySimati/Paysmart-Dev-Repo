package com.afrimax.paysimati.data.model

data class PayMerchantModel(
    val amount: String,
    val txnFee: String,
    val vat: String,
    val recieiverid:String,
    val note:String,
    val senderId:String,
    val entryBy: String,
    val tillnumber: String? = null

)





