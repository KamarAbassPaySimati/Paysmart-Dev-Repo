package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class SearchMerchantByLocation(
    @SerializedName("data")
    val `MerchantListLocation`: List<MerchantListLocation>,
    @SerializedName("message")
    val message: String?=null,
    @SerializedName("success_status")
    val succesStatus: Boolean
)


data class MerchantListLocation(
    @SerializedName("full_name")
    val merchantName: String? = null,
    @SerializedName("paymaart_id")
    val paymaartId:  String?=null,
    @SerializedName("profile_pic")
    val profilePic: String? = null,
    @SerializedName("street_name")
    val address:  String?=null,
    @SerializedName("till_number")
    val tillNumber:  String?=null,
    @SerializedName("trading_type")
    val tradingType: List<String>
)