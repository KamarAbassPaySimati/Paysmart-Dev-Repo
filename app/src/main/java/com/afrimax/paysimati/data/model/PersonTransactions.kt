package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName


data class PersonTransactions(
    @SerializedName("data") val transactions: List<Transaction>,
    @SerializedName("full_name") val fullName:String?,
    @SerializedName("success_status") val successStatus: Boolean,
    @SerializedName("total_count") val totalCount: Int
)

data class Transaction(
    @SerializedName("created_at") val createdAt: Double,
    @SerializedName("note") val note: String? = null,
    @SerializedName("total_amount") val totalAmount: String,
    @SerializedName("transaction_id") val transactionId: String,
    @SerializedName("type") val transactionType: String,
    val showDate: Boolean = false,
)

