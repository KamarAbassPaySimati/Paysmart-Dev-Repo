package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class SubscriptionPaymentRequestBody(
    @SerializedName("ref_no") val referenceNumber: String?,
    @SerializedName("sub_type") val subType: String?,
    @SerializedName("credential") val credentials: String?,
    @SerializedName("auto_renew") val autoRenew: Boolean
)