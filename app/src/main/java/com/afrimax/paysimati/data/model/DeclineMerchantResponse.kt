package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class DeclineMerchantResponse(
    @SerializedName( "description")
    val message:String
)
