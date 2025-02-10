package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class PayMerchantResponse(
    @SerializedName("data")
    val payMerchantList: List<MerchantList>,
    @SerializedName("message")
    val message: String?=null,
    @SerializedName("success_status")
    val successStatus: Boolean,
    @SerializedName("total_count")
    val totalCount: Int
)


data class MerchantList(
    @SerializedName("paymaart_id")
    val paymaartId: String?=null,
  @SerializedName("full_name")
    val MerchantName: String?=null,
    @SerializedName("street_name")
    val streetName: String?=null,
    @SerializedName("till_number")
    val tillNumber:String,
    @SerializedName("profile_pic")
    val profile_pic: String?=null,
    @SerializedName("user_id")
    val userId: String?=null,
    @SerializedName("created_at")
    val createdAt: String?=null
)


