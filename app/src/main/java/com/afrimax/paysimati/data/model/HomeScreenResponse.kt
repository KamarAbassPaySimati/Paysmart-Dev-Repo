package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class HomeScreenResponse(
    @SerializedName("data")
    val homeScreenData: HomeScreenData,
    @SerializedName("success_status")
    val successStatus: Boolean,
    @SerializedName("transaction_data")
    val transactionData: List<IndividualTransactionHistory>,
    @SerializedName("user_transaction")
    val payPersonData: List<PayPersonTransactions>
)

data class HomeScreenData(
    @SerializedName("citizen")
    val citizen: String,
    @SerializedName("completed")
    val completed: Boolean,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("kyc_status")
    val kycStatus: String,
    @SerializedName("kyc_type")
    val kycType: String,
    @SerializedName("membership")
    val membership: String,
    @SerializedName("paymaart_id")
    val paymaartId: String,
    @SerializedName("profile_pic")
    val profilePic: String,
    @SerializedName("public_profile")
    val publicProfile: Boolean,
    @SerializedName("rejection_reasons")
    val rejectionReasons: ArrayList<String>?,
    @SerializedName("account_balance")
    val accountBalance: String
)

data class PayPersonTransactions(
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("paymaart_id")
    val paymaartId: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("profile_pic")
    val profilePic: String
)