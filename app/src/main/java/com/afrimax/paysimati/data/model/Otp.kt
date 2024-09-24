package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class SendOtpRequestBody(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("middle_name")
    val middleName: String,
    @SerializedName("last_name")
    val lastName: String,
    val type: String,
    val value: String,
    @SerializedName("country_code")
    val countryCode: String
)

data class VerifyOtpRequestBody(
    val otp: String,
    val token: String
)

data class SendOtpResponse(
    @SerializedName("success_status")
    val successStatus: Boolean,
    val token: String,
    val message: String
)

data class VerifyOtpResponse(
    @SerializedName("success_status")
    val successStatus: Boolean,
    @SerializedName("record_id")
    val recordId: String,
    val message: String
)

data class ResendCredentialsRequest(
    val email:String
)
