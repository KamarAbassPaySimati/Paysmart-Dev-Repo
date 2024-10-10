package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class UpdateAutoRenewalRequestBody(
    @SerializedName("auto_renew")
    val autoRenew: Boolean
)
