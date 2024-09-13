package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class FlagTransactionRequest(
    @SerializedName("transaction_id")
    val transactionId: String,
    val reasons: ArrayList<String>
)
