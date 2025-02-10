package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class DeclineMerchantRequest(
    @SerializedName("request_id")
    val requestId: String,
    @SerializedName("reciever_id")
    val recieverId: String
)
