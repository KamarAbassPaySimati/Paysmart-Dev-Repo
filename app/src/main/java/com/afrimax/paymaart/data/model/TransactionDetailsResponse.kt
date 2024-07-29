package com.afrimax.paymaart.data.model
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
    val grossTransactionFee: Int,
    @SerializedName("netTransactionFee")
    val netTransactionFee: Double,
    @SerializedName("vatAmount")
    val vatAmount: Double
)