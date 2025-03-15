package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class ScanQrResponse(
    @SerializedName("data")
    val paymerchant:ScanQrPayment,
    @SerializedName("success_status")
    val successStatus: Boolean,
    @SerializedName("message")
    val message: String,
)

data class ScanQrPayment(
    @SerializedName("merchant_id")
    val paymaartId:String?=null,
    @SerializedName("trading_name")
    val tradingName:String?=null,
    @SerializedName("street_name")
    val tradingAddress:String?=null,
    @SerializedName("till_number")
    val tillNumber:String?=null,
    @SerializedName("profile_pic")
    val profilePic:String?=null,
    @SerializedName("full_name")
    val merchantName:String?=null,
    @SerializedName("trading_type")
    val tradingType:String?=null


)


