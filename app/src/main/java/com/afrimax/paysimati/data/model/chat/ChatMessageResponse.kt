package com.afrimax.paysimati.data.model.chat

import com.google.gson.annotations.SerializedName


data class ChatMessageResponse(
@SerializedName("action") val action: String? = null,
@SerializedName("sender_id") val senderId: String? = null,
@SerializedName("receiver_id") val receiverId: String? = null,
@SerializedName("type") val type: String? = null,
@SerializedName("message") val message: String,
@SerializedName("note") val note: String? = null,
@SerializedName("transaction_amount") val transactionAmount: Double? = null,
@SerializedName("created_at") val createdAt: Double? = null,
@SerializedName("request_id") val requestId: String? = null,
@SerializedName("till_number") val tillnumber: String? = null
)
