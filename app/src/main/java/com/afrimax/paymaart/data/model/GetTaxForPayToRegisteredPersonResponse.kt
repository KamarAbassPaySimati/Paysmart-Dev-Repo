package com.afrimax.paymaart.data.model

import com.google.gson.annotations.SerializedName


data class GetTaxForPayToRegisteredPersonResponse(
    @SerializedName("total_amount") val totalAmount: Double = 0.0,
    @SerializedName("transaction_fee") val transactionFee: Double = 0.0,
    @SerializedName("vat") val vat: Double = 0.0
)