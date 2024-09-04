package com.afrimax.paymaart.data.model

import com.google.gson.annotations.SerializedName


data class TransactionHistoryResponse(
    @SerializedName("currentPage") val currentPage: Int = 0,
    @SerializedName("data") val transactionHistory: List<IndividualTransactionHistory> = listOf(),
    @SerializedName("limit") val limit: Int = 0,
    @SerializedName("nextPage") val nextPage: Int? = null,
    @SerializedName("previousPage") val previousPage: Int? = null,
    @SerializedName("success_status") val successStatus: Boolean = false,
    @SerializedName("totalPages") val totalPages: Int = 0,
    @SerializedName("totalRecords") val totalRecords: Int = 0
)

data class IndividualTransactionHistory(
    @SerializedName("commission") val commission: String = "",
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("entered_by") val enteredBy: String? = "",
    @SerializedName("entered_by_name") val enteredByName: String? = "",
    @SerializedName("receiver_id") val receiverId: String = "",
    @SerializedName("receiver_name") val receiverName: String = "",
    @SerializedName("receiver_profile_pic") val receiverProfilePic: String? = null,
    @SerializedName("sender_id") val senderId: String? = "",
    @SerializedName("sender_name") val senderName: String? = "",
    @SerializedName("sender_profile_pic") val senderProfilePic: String? = null,
    @SerializedName("transaction_amount") val transactionAmount: String? = "",
    @SerializedName("transaction_fee") val transactionFee: String? = null,
    @SerializedName("transaction_id") val transactionId: String = "",
    @SerializedName("transaction_type") val transactionType: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("vat") val vat: String? = null,
    val viewType: String? = ""
)