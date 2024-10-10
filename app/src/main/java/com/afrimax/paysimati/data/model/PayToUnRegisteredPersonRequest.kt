package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName


data class PayToUnRegisteredPersonRequest(
    @SerializedName("amount") val amount: Double = 0.0,
    @SerializedName("call_type") val callType: Boolean = false,
    @SerializedName("phone_number") val phoneNumber: String = "",
    @SerializedName("sender_id") val senderId: String = "",
    @SerializedName("receiver_name") val receiverName: String,
    @SerializedName("note") val note: String,
    @SerializedName("password") val password: String?
)