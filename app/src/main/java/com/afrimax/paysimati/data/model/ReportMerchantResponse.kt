package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class ReportMerchantResponse(
    @SerializedName("success_status")
    val successStatus: Boolean = false,
)
