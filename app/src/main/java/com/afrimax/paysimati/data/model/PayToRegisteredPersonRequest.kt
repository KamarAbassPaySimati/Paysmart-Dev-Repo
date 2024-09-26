package com.afrimax.paysimati.data.model
import com.google.gson.annotations.SerializedName


data class PayToRegisteredPersonRequest(
    @SerializedName("credential")
    val credential: String? = "",
    @SerializedName("note")
    val note: String? = "",
    @SerializedName("paymaart_id")
    val paymaartId: String = "",
    @SerializedName("transaction_amount")
    val transactionAmount: Double = 0.0
)