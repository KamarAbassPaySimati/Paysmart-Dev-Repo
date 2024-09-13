package com.afrimax.paysimati.data.model
import com.google.gson.annotations.SerializedName


data class TransactionDetailsResponse(
    @SerializedName("data")
    val transactionDetails: TransactionDetails,
    @SerializedName("message")
    val message: String,
    @SerializedName("success_status")
    val successStatus: Boolean
)

data class TransactionDetails(
    @SerializedName("grossTransactionFee")
    val grossTransactionFee: Double,
    @SerializedName("netTransactionFee")
    val netTransactionFee: Double,
    @SerializedName("vatAmount")
    val vatAmount: Double,
    @SerializedName("total")
    val totalAmount: String,
)