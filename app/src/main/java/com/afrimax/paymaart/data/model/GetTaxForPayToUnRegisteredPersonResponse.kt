package com.afrimax.paymaart.data.model
import com.google.gson.annotations.SerializedName


data class GetTaxForPayToUnRegisteredPersonResponse(
    @SerializedName("data")
    val data: Data = Data(),
    @SerializedName("message")
    val message: String = ""
)

data class Data(
    @SerializedName("gross_transaction_fee")
    val grossTransactionFee: String = "",
    @SerializedName("net_transaction_fee")
    val netTransactionFee: String = "",
    @SerializedName("total_amount")
    val totalAmount: String = "",
    @SerializedName("vat_amount")
    val vatAmount: String = ""
)