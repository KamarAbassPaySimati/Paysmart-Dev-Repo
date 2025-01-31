package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName



data class PreviousChatResponse(
    @SerializedName("data") val chatMessages: List<ChatMessage?>? = null,
    @SerializedName("limit") val limit: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("success_status") val successStatus: Boolean? = null,
    @SerializedName("total_pages") val totalPages: Int? = null,
    @SerializedName("total_records") val totalRecords: Int? = null
) {
    data class ChatMessage(
        @SerializedName("chat_type") val chatType: String? = null,
        @SerializedName("content") val content: String? = null,
        @SerializedName("created_at") val createdAt: String? = null,
        @SerializedName("receiver_id") val receiverId: String? = null,
        @SerializedName("record_id") val recordId: String? = null,
        @SerializedName("sender_id") val senderId: String? = null,
        @SerializedName("transaction_amount") val transactionAmount: String? = null,
        @SerializedName("transaction_id") val transactionId: String? = null,
    )
}