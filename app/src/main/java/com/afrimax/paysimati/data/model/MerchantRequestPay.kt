package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class MerchantRequestPay(
    @SerializedName("request_id")val requestId: String,
    @SerializedName("sender_id") val senderId: String,
    @SerializedName("reciever_id") val receiverId: String,
    @SerializedName("amount") val amount: Double? = null,
    @SerializedName("entry_by") val entryBy: String,
    @SerializedName("flag") val flag: Boolean=false,
    @SerializedName("note")val note:String?=null,
    @SerializedName("encrypted_password")val password:String?=null,
    @SerializedName("till_number")val tillnumber:String?=null,

)
