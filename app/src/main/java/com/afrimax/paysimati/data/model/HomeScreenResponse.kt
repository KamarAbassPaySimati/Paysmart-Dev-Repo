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
    val payPersonData: List<PayPersonTransactions>,
    @SerializedName("merchant_transaction")
    val payMerchantData:List<MerchantTransaction>
)

data class MerchantTransaction(
    @SerializedName("paymaart_id")
    val paymaatId:String?=null,
    @SerializedName("full_name")
    val tradingName:String?=null,
    @SerializedName("street_name")
    val address:String?=null,
    @SerializedName("till_number")
    val tillNumber:String?=null,
    @SerializedName("profile_pic")
    val profilePic:String?=null,
    @SerializedName("user_id")
    val userId:String?=null,
    @SerializedName("created_at")
    val createdAt:String?=null
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