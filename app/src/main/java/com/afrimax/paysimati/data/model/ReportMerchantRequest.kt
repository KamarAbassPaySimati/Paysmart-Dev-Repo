package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class ReportMerchantRequest(
    val images: List<String>?=null,
    val reasons: List<String>,
    @SerializedName("user_id")
    val userId: String
)
