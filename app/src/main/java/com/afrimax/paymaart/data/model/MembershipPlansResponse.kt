package com.afrimax.paymaart.data.model


import com.google.gson.annotations.SerializedName

data class MembershipPlansResponse(
    @SerializedName("data")
    val membershipPlans: List<MembershipPlan>,
    @SerializedName("success_status")
    val successStatus: Boolean
)

data class MembershipPlan(
    @SerializedName("ref_no")
    val referenceNumber: String?,
    @SerializedName("go")
    val go: String?,
    @SerializedName("prime")
    val prime: String?,
    @SerializedName("primex")
    val primeX: String?,
    @SerializedName("service_beneficiary")
    val serviceBeneficiary: String?,
    @SerializedName("subtitle")
    val subtitle: String?
)