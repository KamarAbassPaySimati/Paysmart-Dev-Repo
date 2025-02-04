package com.afrimax.paysimati.data.model


import com.google.gson.annotations.SerializedName

data class GetTaxForPayToMerchantResponse(
    @SerializedName("data")
    val paymerchant:paymerchant,
    @SerializedName("message")
    val message: String,
    @SerializedName("success_status")
    val successStatus: Boolean

)
data class paymerchant(
    @SerializedName("customer_name")
    val customerName:String,
    @SerializedName("customer_id")
    val customerId:String,
    @SerializedName("merchant_name")
    val merchantName:String,
    @SerializedName("merchant_trading_name")
    val merchantTradingName:String,
    @SerializedName("merchant_id")
    val merchantId:String,
    @SerializedName("transaction_id")
    val transactionID:String,
    @SerializedName("created_at")
    val createdAt:Int,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("till_number")
    val tillnumber:String,
    @SerializedName("gross_transaction_fee")
    val grosstxnfee: String,
    @SerializedName("transaction_fee")
    val transactionfee: String,
    @SerializedName("vat")
    val vat: String
)
