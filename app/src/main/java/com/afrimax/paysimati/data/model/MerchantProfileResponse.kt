package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class MerchantProfileResponse(
    @SerializedName("data")
    val data: MerchantProfile? = null,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("success_status")
    val success_status: Boolean = false
)







data class MerchantProfile(
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("paymaart_id")
    val paymaartId: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("till_number")
    val tillNumber: List<String>,
    @SerializedName("trading_district")
    val tradingDistrict: String,
    @SerializedName("trading_house_name")
    val tradingHouseName: String,
    @SerializedName("trading_image")
    val tradingImage: List<String>,
    @SerializedName("trading_images")
    val tradingImages: List<String>,
    @SerializedName("trading_name")
    val tradingName: String,
    @SerializedName("trading_street_name")
    val tradingstreetName: String,
    @SerializedName("trading_town_village_ta")
    val tradingVillage: String,
    @SerializedName("trading_type")
    val tradingType: List<String>
)