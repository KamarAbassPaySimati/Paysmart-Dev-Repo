package com.afrimax.paysimati.data.model


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GetTaxForPayToMerchantResponse(
    @SerializedName("data")
    val paymerchant:PayMerchantPaymentResponse,
    @SerializedName("message")
    val message: String,
    @SerializedName("success_status")
    val successStatus: Boolean

)
data class PayMerchantPaymentResponse(
    @SerializedName("customer_name")
    val customerName:String?="",
    @SerializedName("customer_id")
    val customerId:String?="",
    @SerializedName("merchant_name")
    val merchantName:String?="",
    @SerializedName("merchant_trading_name")
    val merchantTradingName:String?="",
    @SerializedName("merchant_id")
    val merchantId:String?="",
    @SerializedName("transaction_id")
    val transactionID:String?="",
    @SerializedName("created_at")
    val createdAt:Long,
    @SerializedName("amount")
    val amount:Double=0.0,
    @SerializedName("till_number")
    val tillnumber:String?="",
    @SerializedName("gross_transaction_fee")
    val grosstxnfee: String?="",
    @SerializedName("transaction_fee")
    val transactionfee: String?="",
    @SerializedName("vat")
    val vat: Double = 0.0,
    @SerializedName("note")
    val note: String?=""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(customerName)
        parcel.writeString(customerId)
        parcel.writeString(merchantName)
        parcel.writeString(merchantTradingName)
        parcel.writeString(merchantId)
        parcel.writeString(transactionID)
        parcel.writeLong(createdAt)
        parcel.writeDouble(amount)
        parcel.writeString(tillnumber)
        parcel.writeString(grosstxnfee)
        parcel.writeString(transactionfee)
        parcel.writeDouble(vat)
        parcel.writeString(note)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PayMerchantPaymentResponse> {
        override fun createFromParcel(parcel: Parcel): PayMerchantPaymentResponse {
            return PayMerchantPaymentResponse(parcel)
        }

        override fun newArray(size: Int): Array<PayMerchantPaymentResponse?> {
            return arrayOfNulls(size)
        }
    }
}
