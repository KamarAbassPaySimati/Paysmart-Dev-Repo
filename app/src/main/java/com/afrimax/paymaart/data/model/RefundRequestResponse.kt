package com.afrimax.paymaart.data.model

import com.google.gson.annotations.SerializedName

data class RefundRequestResponse(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("data")
    val refundRequest: List<RefundRequest>,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("nextPage")
    val nextPage: Any,
    @SerializedName("previousPage")
    val previousPage: Any,
    @SerializedName("success_status")
    val successStatus: Boolean,
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("totalRecords")
    val totalRecords: Int
)

data class RefundRequest(
    @SerializedName("receiver_name")
    val receiverName: String,
    @SerializedName("receiver_id")
    val receiverId: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("transaction_id")
    val transactionId: String,
    @SerializedName("transaction_type")
    val transactionType: String,
    @SerializedName("profile_pic")
    val profilePic: String? = null
)