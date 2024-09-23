package com.afrimax.paymaart.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class PayToUnRegisteredPersonResponse(
    @SerializedName("data") val data: PayUnRegisteredPersonResponse,
    @SerializedName("message") val message: String = ""
)

@Parcelize
data class PayUnRegisteredPersonResponse(
    @SerializedName("amount") val amount: String? = "",
    @SerializedName("created_at") val createdAt: Double = 0.0,
    @SerializedName("note") val note: String? = "",
    @SerializedName("phone_number") val phoneNumber: String? = "",
    @SerializedName("receiver_name") val receiverName: String? = "",
    @SerializedName("sender_id") val senderId: String? = "",
    @SerializedName("sender_name") val senderName: String? = "",
    @SerializedName("transaction_fees") val transactionFees: String? = "",
    @SerializedName("transaction_id") val transactionId: String? = "",
    @SerializedName("receiver_id") val receiverId: String? = null,
    @SerializedName("vat_amount") val vatAmount: String? = ""
) : Parcelable