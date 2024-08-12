package com.afrimax.paymaart.data.model
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class PersonTransactions(
    @SerializedName("data")
    val transactions: List<Transaction>,
    @SerializedName("success_status")
    val successStatus: Boolean,
    @SerializedName("total_count")
    val totalCount: Int
)

data class Transaction(
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("note")
    val note: String? = null,
    @SerializedName("total_amount")
    val totalAmount: String,
    @SerializedName("transaction_id")
    val transactionId: String,
    @SerializedName("type")
    val transactionType: String,
    val showDate: Boolean = false,
)

