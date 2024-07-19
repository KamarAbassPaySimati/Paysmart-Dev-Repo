package com.afrimax.paymaart.data.model


import com.google.gson.annotations.SerializedName

data class SubscriptionDetailsResponse(
    @SerializedName("data")
    val subscriptionDetails: SubscriptionDetails,
    @SerializedName("success_status")
    val successStatus: Boolean
)

data class SubscriptionDetails(
    @SerializedName("total_amount")
    val totalAmount: Double,
    @SerializedName("transaction_fee")
    val transactionFee: Double,
    @SerializedName("vat")
    val vat: Double
)

data class SubscriptionDetailsRequestBody(
    @SerializedName("ref_no")
    val referenceNumber: String?,
    @SerializedName("sub_type")
    val subType: String?,
    @SerializedName("auto_renew")
    val autoRenew: Boolean
)