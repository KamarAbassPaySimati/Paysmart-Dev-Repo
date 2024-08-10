package com.afrimax.paymaart.data.model
import com.google.gson.annotations.SerializedName


data class PayPersonResponse(
    @SerializedName("data")
    val payPersonList: List<PayPerson>,
    @SerializedName("success_status")
    val successStatus: Boolean,
    @SerializedName("total_count")
    val totalCount: Int
)

data class PayPerson(
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("paymaart_id")
    val paymaartId: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("profile_pic")
    val profilePicture: String? = null
)

data class PayPersonRequestBody(
    @SerializedName("phone_number")
    val phoneNumber: List<String>
)