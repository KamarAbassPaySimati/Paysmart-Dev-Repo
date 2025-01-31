package com.afrimax.paysimati.data.model.chat

import com.google.gson.annotations.SerializedName


data class ChatMessageRequest(
    @SerializedName("action") val action: String,
    @SerializedName("sender_id") val senderId: String,
    @SerializedName("receiver_id") val receiverId: String,
    @SerializedName("message") val message: String? = null,
)