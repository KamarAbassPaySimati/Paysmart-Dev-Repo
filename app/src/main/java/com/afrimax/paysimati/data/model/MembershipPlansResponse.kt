package com.afrimax.paysimati.data.model


import com.google.gson.annotations.SerializedName

data class MembershipPlansResponse(
    @SerializedName("data")
    val membershipPlans: List<MembershipPlan>,
    @SerializedName("success_status")
    val successStatus: Boolean,
    @SerializedName("user_data")
    val userData: MembershipUserData?
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

data class MembershipUserData(
    @SerializedName("membership")
    val membership: String?,
    @SerializedName("auto_renew")
    val autoRenew: Boolean,
    @SerializedName("membership_start")
    val membershipStart: Long,
    @SerializedName("membership_expiry")
    val membershipExpiry: Long,
    @SerializedName("membership_id")
    val membershipId: String?
)